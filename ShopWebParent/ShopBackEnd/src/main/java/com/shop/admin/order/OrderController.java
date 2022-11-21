package com.shop.admin.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shop.admin.setting.SettingService;
import com.shop.common.entity.order.Order;
import com.shop.common.entity.setting.Setting;

@Controller
public class OrderController {

	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;
	
	@GetMapping("/orders")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return listByPage(1, "orderTime", "desc", null, model, request);
	}
	@GetMapping("orders/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			String sortField, String sortDir, String keyword, Model model, HttpServletRequest request) {
		Page<Order> page = orderService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Order> listOrders = page.getContent();
		
		long startCount = (pageNum -1) * OrderService.ORDERS_PER_PAGE + 1;
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		loadCurrencySetting(request);
		
		return "orders/orders";
	}

	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();
		
		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
		
	}
	
	@GetMapping("orders/detail/{id}")
	public String viewOrderDetails(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes attributes, HttpServletRequest request) {
		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);
			model.addAttribute("order", order);
			
			return "orders/order_details_modal";
		} catch (OrderNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/orders";
		}
	}
	
	@GetMapping("orders/delete/{id}")
	public String deleteOrder(@PathVariable(name = "id") Integer id, RedirectAttributes attributes) {
		try {
			orderService.delete(id);
			attributes.addFlashAttribute("message", "Hóa đơn có ID: " + id + " đã được xóa");
		} catch (OrderNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/orders";
	}
}
