package com.shop.shoppingcart;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shop.Utility;
import com.shop.common.entity.CartItem;
import com.shop.common.entity.Customer;
import com.shop.customer.CustomerService;

@Controller
public class ShoppingCartController {

	@Autowired private ShoppingCartService cartService;
	@Autowired private CustomerService customerService;
	
	@GetMapping("/cart")
	public String viewCart(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);

		List<CartItem> listCartItems = cartService.listCartItems(customer);
		
		float estimatesTotal = 0.0f;
		
		for (CartItem cartItem : listCartItems) {
			estimatesTotal += cartItem.getSubTotal();
		}
		
		model.addAttribute("listCartItems", listCartItems);
		model.addAttribute("estimatesTotal", estimatesTotal);

		return "cart/shopping_cart";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}
}
