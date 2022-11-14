package com.shop.address;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.common.entity.Address;
import com.shop.common.entity.Customer;

@Service
@Transactional
public class AddressService {

	@Autowired private AddressRepository repo;
	
	public List<Address> listAddressBook(Customer customer) {
		return repo.findByCustomer(customer);
	}
	
	public void save(Address address) {
		repo.save(address);
	}
	
	public Address getByIdAndCustomer(Integer addressId, Integer customerId) throws AddressNotFoundException {
		try {
			Address address = repo.findByIdAndCustomer(addressId, customerId);
			return address;
		} catch (NoSuchElementException e) {
			throw new AddressNotFoundException("Không tồn tại địa chỉ có ID: " + addressId);
		}
		
	}
	
	public void delete(Integer addressId, Integer customerId) throws AddressNotFoundException {
		try {
			repo.deleteByIdAndCustomer(addressId, customerId);
		} catch (Exception e) {
			throw new AddressNotFoundException("Không tồn tại địa chỉ có ID: " + addressId);
		}
	}
	
	public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
		if(defaultAddressId > 0) {
			repo.setDefaultAddress(defaultAddressId);
		}
		repo.setNonDefaultForOthers(defaultAddressId, customerId);
	}
}
