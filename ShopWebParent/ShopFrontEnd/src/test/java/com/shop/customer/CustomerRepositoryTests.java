package com.shop.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.AuthenticationType;
import com.shop.common.entity.Country;
import com.shop.common.entity.Customer;
import com.shop.customer.CustomerRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {
	
	@Autowired CustomerRepository repo;
	
	@Autowired EntityManager entityManager;
	
	@Test
	public void testCreateCustomer() {
		Customer customer = new Customer();
		Integer countryId = 242;
		Country country = entityManager.find(Country.class, countryId);
		
		customer.setEmail("anh01652851901@gmail.com");
		customer.setFirstName("Trần");
		customer.setLastName("Tuấn Anh");
		customer.setPassword("Trang191919");
		customer.setPhoneNumber("0352851901");
		customer.setCountry(country);
		customer.setState("Hà Nội");
		customer.setCity("Hai Bà Trưng");
		customer.setAddressDetail("36 Chùa Liên Phái, Cầu Dền");

		customer.setPostalCode("112200");
		customer.setCreatedTime(new Date());
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateCustomer2() {
		Customer customer = new Customer();
		Integer countryId = 242;
		Country country = entityManager.find(Country.class, countryId);
		
		customer.setEmail("dttthuytrang0109@gmail.com");
		customer.setFirstName("Đặng");
		customer.setLastName("Thùy Trang");
		customer.setPassword("Trang191919");
		customer.setPhoneNumber("0376076115");
		customer.setCountry(country);
		customer.setState("Hà Nội");
		customer.setCity("Cầu Giấy");
		customer.setAddressDetail("445 Nguyễn Khang, Yên Hòa");

		customer.setPostalCode("122000");
		customer.setCreatedTime(new Date());
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateCustomer3() {
		Customer customer = new Customer();
		Integer countryId = 242;
		Country country = entityManager.find(Country.class, countryId);
		
		customer.setEmail("phamtest@gmail.com");
		customer.setFirstName("Phạm");
		customer.setLastName("Thị Test");
		customer.setPassword("Test123");
		customer.setPhoneNumber("098765432");
		customer.setCountry(country);
		customer.setState("Hà Nội");
		customer.setCity("Đống Đa");
		customer.setAddressDetail("1 Xã Đàn");

		customer.setPostalCode("123000");
		customer.setCreatedTime(new Date());
		customer.setVerificationCode("1234");
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListCustomer() {
		Iterable<Customer> customers = repo.findAll();
		customers.forEach(System.out::println);
		
		assertThat(customers).hasSizeGreaterThan(1);
	}
	
	@Test
	public void testUpdateCustomer() {
		Integer customerId = 3;
		String lastName = "TestUpdate";
		
		Customer customer = repo.findById(customerId).get();
		customer.setLastName(lastName);
		customer.setEnabled(true);
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer.getLastName()).isEqualTo(lastName);
	}
	
	@Test
	public void testGetCustomer() {
		Integer customerId = 2;
		Optional<Customer> findById = repo.findById(customerId);
		
		assertThat(findById).isPresent();
		
		Customer customer = findById.get();
		System.out.println(customer);
	}
	
	@Test
	public void testDeleteCustomer() {
		Integer customerId = 3;
		repo.deleteById(customerId);
		
		Optional<Customer> findById = repo.findById(customerId);
		
		assertThat(findById).isNotPresent();
	}
	
	@Test
	public void testFindByEmail() {
		String email = "dttthuytrang0109@gmail.com";
		
		Customer customer = repo.findByEmail(email);
		
		assertThat(customer.getEmail()).isEqualTo(email);
		
		System.out.println(customer);
	}
	
	@Test
	public void testFindByVerificationCode() {
		String verificationCode = "1234";
		
		Customer customer = repo.findByVerificationCode(verificationCode);
		
		assertThat(customer.getVerificationCode()).isEqualTo(verificationCode);
		
		System.out.println(customer);
	}

	@Test
	public void testEnableCustomer() {
		Integer customerId = 1;
		
		repo.enable(customerId);
		
		Customer enabledCustomer = repo.findById(customerId).get();
		
		assertThat(enabledCustomer.isEnabled()).isTrue();
		
	}
	
	@Test
	public void testUpdateAuthenticationType() {
		Integer id = 1;
		repo.updateAuthenticationType(id, AuthenticationType.DATABASE);
		
		Customer customer = repo.findById(id).get();
		assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.FACEBOOK);
		
	}

}
