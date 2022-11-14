package com.shop.address;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shop.Utility;
import com.shop.common.entity.Address;
import com.shop.common.entity.Country;
import com.shop.common.entity.Customer;
import com.shop.customer.CustomerService;

@Controller
public class AddressController {

	@Autowired private AddressService addressService;
	@Autowired private CustomerService customerService;
	
	@GetMapping("/address_book")
	public String showAddressBook(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<Address> listAddresses = addressService.listAddressBook(customer);
		
		boolean usePrimaryAddressAsDefault = true;
		
		for (Address address : listAddresses) {
			if(address.isDefaultForShipping()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}
		
		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("customer", customer);
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		return "address_book/addresses";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}
	
	@GetMapping("/address_book/new")
	public String newAddress(Model model) {
		Address address = new Address();
		List<Country> listAllCountries = customerService.listAllCountries();
		
		model.addAttribute("address", address);
		model.addAttribute("listAllCountries", listAllCountries);
		model.addAttribute("pageTitle", "Thêm địa chỉ mới");
		
		return "address_book/address_form";
	}
	
	@PostMapping("/address_book/save")
	public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes attributes) {
		Customer customer = getAuthenticatedCustomer(request);
		address.setCustomer(customer);
		
		if(address.getId() == null) {
			attributes.addFlashAttribute("message", "Thêm địa chỉ mới thành công.");
		} else {
			attributes.addFlashAttribute("message", "Thông tin địa chỉ đã được cập nhật.");
		}
		
		addressService.save(address);
		
		
		
		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable(name = "id") Integer id, Model model, HttpServletRequest request, RedirectAttributes attributes) {
		Customer customer = getAuthenticatedCustomer(request);
		try {
			Address address = addressService.getByIdAndCustomer(id, customer.getId());
			List<Country> listAllCountries = customerService.listAllCountries();
			
			model.addAttribute("address", address);
			model.addAttribute("listAllCountries", listAllCountries);
			model.addAttribute("pageTitle", "Chỉnh sửa địa chỉ");
			return "address_book/address_form";
		} catch (AddressNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/address_book";
		}
		
		
	}
	
	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable(name = "id") Integer id, HttpServletRequest request, RedirectAttributes attributes) {
		Customer customer = getAuthenticatedCustomer(request);
		try {
			addressService.delete(id, customer.getId());
			attributes.addFlashAttribute("message", "Địa chỉ có ID: " + id + " đã được xóa.");
		} catch (AddressNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(id, customer.getId());
		
		return "redirect:/address_book";
	}
}
