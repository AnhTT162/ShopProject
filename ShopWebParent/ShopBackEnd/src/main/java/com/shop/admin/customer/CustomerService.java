package com.shop.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shop.admin.setting.country.CountryRepository;
import com.shop.common.entity.Country;
import com.shop.common.entity.Customer;
import com.shop.common.exception.CustomerNotFoundException;

@Service
@Transactional
public class CustomerService {
	public static final int CUSTOMERS_PER_PAGE = 2;

	@Autowired private CustomerRepository customerRepo;
	@Autowired private CountryRepository countryRepo;
	@Autowired PasswordEncoder passwordEncoder;
	
	public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMERS_PER_PAGE, sort);
		if(keyword != null) {
			return customerRepo.findAll(keyword, pageable);
		}
		return customerRepo.findAll(pageable);	
	}
	
	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepo.updateEnableStatus(id, enabled);
	}
	
	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Không tồn tại Khách hàng có ID: " + id);
		}
	}
	
	public List<Country> listAllCountry() {
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public void save(Customer customerInForm) {
		Customer customerInDb = customerRepo.findById(customerInForm.getId()).get();
		if(!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);
		} else {
			customerInForm.setPassword(customerInDb.getPassword());
		}
		
		customerInForm.setEnabled(customerInDb.isEnabled());
		customerInForm.setVerificationCode(customerInDb.getVerificationCode());
		customerInForm.setCreatedTime(customerInDb.getCreatedTime());
		customerInForm.setAuthenticationType(customerInDb.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDb.getResetPasswordToken());
		
		customerRepo.save(customerInForm);
	}
	
	public void delete(Integer id) throws CustomerNotFoundException {
		Long countById = customerRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new CustomerNotFoundException("Khách hàng có ID: " + id + " không tồn tại");
		}else {
			customerRepo.deleteById(id);
		}
	}
}
