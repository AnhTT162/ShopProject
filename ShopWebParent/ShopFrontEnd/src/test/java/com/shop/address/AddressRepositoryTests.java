package com.shop.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Address;
import com.shop.common.entity.Country;
import com.shop.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {

	@Autowired private AddressRepository repo;

	@Test
	public void testAddNew() {
		Integer customerId = 23;
		Integer countryId = 242;
		
		Address address = new Address();
		
		address.setCustomer(new Customer(customerId));
		address.setCountry(new Country(countryId));
		address.setAddressDetail("26 Chùa Liên Phái, Cầu Dền");
		address.setCity("Hai Bà Trưng");
		address.setDefaultForShipping(false);
		address.setFirstName("Trần");
		address.setLastName("Tuấn Anh");
		address.setPhoneNumber("0368671602");
		address.setPostalCode("1234");
		address.setState("Hà Nội");
		
		Address savedAddress = repo.save(address);
		
		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}
	
	@Test
	public void  testFindByCustomer() {
		Integer customerId = 23;
		
		List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
		
		assertThat(listAddresses.size()).isGreaterThan(0);
		
		listAddresses.forEach(System.out::println);
	}
	
	@Test
	public void testFindByIdAndCustomer() {
		Integer customerId = 23;
		Integer addressId = 2;
		
		Address address = repo.findByIdAndCustomer(addressId, customerId);
		
		assertThat(address).isNotNull();
		System.out.println(address);
	}
	
	@Test
	public void testUpdate() {
		Integer addressId = 2;
		//String lastName = "Thị Thùy Trang";
		
		Address address = repo.findById(addressId).get();
		//address.setLastName(lastName);
		address.setDefaultForShipping(true);
		
		Address updatedAddress = repo.save(address);
		
		//assertThat(updatedAddress.getLastName()).isEqualTo(lastName);
	}
	
	@Test
	public void testDeleteByIdAndCustomer() {
		Integer customerId = 23;
		Integer addressId = 3;
		
		repo.deleteByIdAndCustomer(addressId, customerId);
		
		Address address = repo.findByIdAndCustomer(addressId, customerId);
		
		assertThat(address).isNull();
	}
	
	@Test
	public void testSetDefaultAddress() {
		Integer id = 4;
		repo.setDefaultAddress(id);
		
		Address address = repo.findById(id).get();
		
		assertThat(address.isDefaultForShipping()).isTrue();
		
		
	}
	
	@Test
	public void testSetNonDefaultAddress() {
		Integer id = 4;
		Integer cusId = 23;
		repo.setNonDefaultForOthers(id, cusId);
		

		
		
	}
	
}
