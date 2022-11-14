package com.shop.admin.shippingrate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shop.common.entity.Country;
import com.shop.common.entity.ShippingRate;

@Controller
public class ShippingRateController {

	@Autowired private ShippingRateService service;
	
	@GetMapping("/shipping_rates")
	public String listFirstPage(Model model) {
		return listByPage(1, "id", "asc", null, model);
	}
	
	@GetMapping("/shipping_rates/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			String sortField, String sortDir, String keyword, Model model) {
		Page<ShippingRate> page = service.findAll(pageNum, sortField, sortDir, keyword);
		List<ShippingRate> listShippingRates = page.getContent();
		
		long startCount = (pageNum -1) * ShippingRateService.RATES_PER_PAGE + 1;
		long endCount = startCount + ShippingRateService.RATES_PER_PAGE - 1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("listShippingRates", listShippingRates);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		
		return "shipping_rates/shipping_rates";
	}
	
	@GetMapping("/shipping_rates/new")
	public String newRate(Model model) {
		List<Country> listAllCountries = service.listAllCountries();
		
		model.addAttribute("listAllCountries", listAllCountries);
		model.addAttribute("rate", new ShippingRate());
		model.addAttribute("pageTitle", "Thêm mới phí giao hàng");
		
		return "shipping_rates/shipping_rate_form";
	}
	
	@PostMapping("shipping_rates/save")
	public String saveRate(ShippingRate rate, RedirectAttributes attributes) {
		try {
			service.save(rate);
			attributes.addFlashAttribute("message", "Thông tin địa chỉ giao hàng đã được lưu.");
		} catch (ShippingRateAlreadyExistsException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/shipping_rates";
	}
	
	@GetMapping("/shipping_rates/edit/{id}")
	public String editRate(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			ShippingRate rate = service.get(id);
			List<Country> listAllCountries = service.listAllCountries();
			model.addAttribute("listAllCountries", listAllCountries);
			model.addAttribute("rate", rate);
			model.addAttribute("pageTitle", "Chỉnh sửa phí giao hàng");
			
			return "shipping_rates/shipping_rate_form";
		} catch (ShippingRateNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/shipping_rates";
		}
	}
	
	@GetMapping("/shipping_rates/delete/{id}")
	public String deleteRate(@PathVariable(name = "id") Integer id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("message", "Địa chỉ có ID: " + id + " đã được xóa.");
		} catch (ShippingRateNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/shipping_rates";
	}
	
	@GetMapping("/shipping_rates/cod/{id}/enabled/{status}/{pageNum}")
	public String updateCategoryEnabledStatus(@PathVariable(name = "pageNum") int pageNum,@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status,
			String sortField, String sortDir, String keyword,
			Model model) {
		try {
			service.updateCODSupport(id, status);
			String codStatus = status ? " đã được kích hoạt" : " đã bị vô hiệu hóa";
			String message = "Hỗ trợ thanh toán khi nhận hàng cho địa chỉ có ID: " + id + codStatus; 
			model.addAttribute("message", message);
		} catch (ShippingRateNotFoundException e) {
			model.addAttribute("message", e.getMessage());
		}
			
		return listByPage(pageNum,sortField, sortDir, keyword, model);
		
	}
}
