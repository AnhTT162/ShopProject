package com.shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shop.common.entity.Customer;
import com.shop.customer.CustomerRepository;

public class CustomerUserDetailsService implements UserDetailsService {
	
	@Autowired private CustomerRepository repo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = repo.findByEmail(email);
		if(customer == null)
			throw new UsernameNotFoundException("Tên đăng nhập: " + email + "không đúng");
		return new CustomerUserDetails(customer);
	}

}
