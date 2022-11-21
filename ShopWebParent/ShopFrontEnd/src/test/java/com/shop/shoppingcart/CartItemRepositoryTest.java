package com.shop.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.CartItem;
import com.shop.common.entity.Customer;
import com.shop.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTest {

	@Autowired private CartItemRepository repository;
	@Autowired private TestEntityManager entityManager;
	
	@Test
	public void testSaveItem() {
		Integer customerId = 2;
		Integer productId = 1;
		
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		CartItem newItem = new CartItem();
		
		newItem.setCustomer(customer);
		newItem.setProduct(product);
		newItem.setQuantity(1);
		
		CartItem savedItem = repository.save(newItem);
		
		assertThat(savedItem.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testSaveItem2() {
		Integer customerId = 23;
		Integer productId = 23;
		
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		CartItem newItem = new CartItem();
		
		newItem.setCustomer(customer);
		newItem.setProduct(product);
		newItem.setQuantity(1);
		
		CartItem savedItem = repository.save(newItem);
		
		assertThat(savedItem.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testSave2Items() {
		Integer customerId = 23;
		Integer productId = 10;
		
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		CartItem item1 = new CartItem();
		item1.setCustomer(customer);
		item1.setProduct(product);
		item1.setQuantity(2);
		
		CartItem item2 = new CartItem();
		item2.setCustomer(new Customer(customerId));
		item2.setProduct(new Product(8));
		item2.setQuantity(3);
		
		Iterable<CartItem> iterable = repository.saveAll(List.of(item1, item2));
		
		assertThat(iterable).size().isGreaterThan(0);
	}
	
	@Test
	public void findByCustomer() {
		Integer customerId = 23;
		List<CartItem> listItems = repository.findByCustomer(new Customer(customerId));
		listItems.forEach(System.out::println);
		assertThat(listItems.size()).isEqualTo(3);
	}
	@Test
	public void findByCustomerAndProduct() {
		Integer customerId = 2;
		Integer productId = 1;
		
		CartItem item = repository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
		System.out.println(item);
		assertThat(item).isNotNull();
	}
	
	@Test
	public void testUpdateQuantity() {
		Integer customerId = 2;
		Integer productId = 1;
		Integer quantity = 4;
		
		repository.updateQuantity(quantity, customerId, productId);
		
		CartItem item = repository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
		
		assertThat(item.getQuantity()).isEqualTo(quantity);
	}
	
	@Test
	public void testDeleteByCustomerAndProduct() {
		Integer customerId = 23;
		Integer productId = 10;
		
		repository.deleteByCustomerAndProduct(customerId, productId);
		
		CartItem item = repository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
		
		assertThat(item).isNull();
		
	}
}
