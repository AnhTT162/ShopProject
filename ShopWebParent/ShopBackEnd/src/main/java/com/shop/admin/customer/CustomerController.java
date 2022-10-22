package com.shop.admin.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shop.common.entity.Country;
import com.shop.common.entity.Customer;
import com.shop.common.entity.User;
import com.shop.common.exception.CustomerNotFoundException;

@Controller
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@GetMapping("/customers")
	public String listFirstPage(Model model) {

		return listByPage(1, "id", "asc", null, model);
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, @Param("sortField") String sortField,
			@Param("sortDir") String sortDir, @Param("keyword") String keyword, Model model) {
		Page<Customer> page = customerService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Customer> listCustomers = page.getContent();

		long startCount = (pageNum - 1) * CustomerService.CUSTOMERS_PER_PAGE + 1;
		long endCount = startCount + CustomerService.CUSTOMERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listCustomers", listCustomers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		return "customers/customers";
	}

	@GetMapping("/customers/{id}/enabled/{status}/{pageNum}")
	public String updateCustomerEnableStatus(@PathVariable(name = "pageNum") int pageNum,
			@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword,
			Model model) {
		customerService.updateCustomerEnabledStatus(id, status);
		String cusStatus = status ? " đã được kích hoạt" : " đã bị vô hiệu hóa";
		String message = "Khách hàng có ID: " + id + cusStatus;
		model.addAttribute("message", message);
		return listByPage(pageNum, sortField, sortDir, keyword, model);
	}

	@GetMapping("/customers/edit/{id}")
	public String updateCustomer(@PathVariable(name = "id") Integer id, Model model) throws CustomerNotFoundException {
		Customer customer = customerService.get(id);
		List<Country> listAllCountries = customerService.listAllCountry();
		model.addAttribute("listAllCountries", listAllCountries);
		model.addAttribute("customer", customer);
		model.addAttribute("pageTitle", "Chỉnh sửa khách hàng (ID: " + id + ")");

		return "customers/customer_form";
	}

	@PostMapping("/customers/save")
	public String save(Customer customer, RedirectAttributes attributes) {
		customerService.save(customer);
		attributes.addFlashAttribute("message",
				"Thông tin khách hàng có ID: " + customer.getId() + " đã được cập nhật");
		return getRedirectURLtoAffectedUser(customer);
	}

	private String getRedirectURLtoAffectedUser(Customer customer) {
		return "redirect:/customers/page/1?sortField=id&sortDir=asc&keyword=" + customer.getEmail();
	}
	
	@GetMapping("/customers/delete/{id}")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes attributes) throws CustomerNotFoundException {
		try {
			customerService.delete(id);
			attributes.addFlashAttribute("message", "Khách hàng có ID: " + id + " đã được xóa");
		} catch (CustomerNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}")
	public String detailCustomer(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Customer customer = customerService.get(id);
			model.addAttribute("customer", customer);
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/customers";
		}
	}
}
