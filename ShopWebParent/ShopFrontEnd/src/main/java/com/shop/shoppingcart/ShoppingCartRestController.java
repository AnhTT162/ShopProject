package com.shop.shoppingcart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.Utility;
import com.shop.common.entity.Customer;
import com.shop.common.exception.CustomerNotFoundException;
import com.shop.customer.CustomerService;

@RestController
public class ShoppingCartRestController {

	@Autowired private ShoppingCartService cartService;
	@Autowired private CustomerService customerService;
	
	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {		
		try {
			Customer customer = getAuthenticatedCustomer(request);
			cartService.addProduct(productId, quantity, customer);
			return "Sản phẩm đã được thêm vào giỏ hàng của bạn.";
		} catch (CustomerNotFoundException e) {
			return "Bạn phải đăng nhập để thêm sản phẩm vào giỏ hàng.";
		}
		
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if(email == null) {
			throw new CustomerNotFoundException("Chưa đăng nhập");
		}
		
		return customerService.getCustomerByEmail(email);
	}
	
	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subTotal = cartService.updateQuantity(productId, quantity, customer);
			return String.valueOf(subTotal);
		} catch (CustomerNotFoundException e) {
			return "Bạn phải đăng nhập để cập nhật số lượng sản phẩm.";
		}
	}
	
	@DeleteMapping("/cart/remove/{productId}")
	public String removeProduct(@PathVariable("productId") Integer productId, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			cartService.removeProduct(productId, customer);
			return "Sản phẩm đã được xóa khỏi giỏ hàng của bạn.";
		} catch (CustomerNotFoundException e) {
			return "Bạn phải đăng nhập để xóa sản phẩm.";
		}
		
		
	}
}
