package com.shop.admin.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Customer;
import com.shop.common.entity.order.Order;
import com.shop.common.entity.order.OrderDetail;
import com.shop.common.entity.order.OrderStatus;
import com.shop.common.entity.order.PaymentMethod;
import com.shop.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTests {

	@Autowired private OrderRepository repo;
	@Autowired private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewOrderWithSingleProduct() {
		Customer customer = entityManager.find(Customer.class, 23);
		Product product = entityManager.find(Product.class, 1);
		
		Order order = new Order();
		
		order.setOrderTime(new Date());
		order.setCustomer(customer);
		
		order.copyAddressFromCustomer();
		
		order.setShippingCost(10);
		order.setProductCost(product.getCost());
		order.setTax(0);
		order.setSubtotal(product.getPrice());
		order.setTotal(product.getPrice() + 10);
		order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		order.setStatus(OrderStatus.NEW);
		order.setDeliverDate(new Date());
		order.setDeliverDays(1);
		
		OrderDetail orderDetail = new OrderDetail();
		
		orderDetail.setProduct(product);
		orderDetail.setOrder(order);
		orderDetail.setProductCost(product.getCost());
		orderDetail.setShippingCost(10);
		orderDetail.setQuantity(1);
		orderDetail.setSubtotal(product.getPrice());
		orderDetail.setUnitPrice(product.getPrice());
		
		order.getOrderDetails().add(orderDetail);
		
		Order savedOrder = repo.save(order);
		
		assertThat(savedOrder.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewOrderWithMultipleProducts() {
		Customer customer = entityManager.find(Customer.class, 25);
		Product product1 = entityManager.find(Product.class, 3);
		Product product2 = entityManager.find(Product.class, 5);
		
		Order order = new Order();
		order.setOrderTime(new Date());
		order.setCustomer(customer);
		order.copyAddressFromCustomer();
		
		OrderDetail orderDetail1 = new OrderDetail();
		orderDetail1.setProduct(product1);
		orderDetail1.setOrder(order);
		orderDetail1.setProductCost(product1.getCost());
		orderDetail1.setShippingCost(10);
		orderDetail1.setQuantity(1);
		orderDetail1.setSubtotal(product1.getPrice());
		orderDetail1.setUnitPrice(product1.getPrice());
		
		OrderDetail orderDetail2 = new OrderDetail();
		orderDetail2.setProduct(product2);
		orderDetail2.setOrder(order);
		orderDetail2.setProductCost(product2.getCost());
		orderDetail2.setShippingCost(20);
		orderDetail2.setQuantity(2);
		orderDetail2.setSubtotal(product2.getPrice() * 2);
		orderDetail2.setUnitPrice(product2.getPrice());
		
		order.getOrderDetails().add(orderDetail1);
		order.getOrderDetails().add(orderDetail2);
		
		order.setShippingCost(30);
		order.setProductCost(product1.getCost() + product2.getCost());
		order.setTax(0);
		float subtotal = product1.getPrice() + product2.getPrice()*2;
		order.setSubtotal(subtotal);
		order.setTotal(subtotal + 30);
		
		order.setPaymentMethod(PaymentMethod.COD);
		order.setStatus(OrderStatus.PROCESSING);
		order.setDeliverDate(new Date());
		order.setDeliverDays(3);
		
		Order savedOrder = repo.save(order);
		
		assertThat(savedOrder.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testLissAll() {
		Iterable<Order> orders = repo.findAll();
		
		assertThat(orders).hasSizeGreaterThan(0);
		
		orders.forEach(System.out::println);
	}
	
	@Test
	public void testUpdate() {
		Integer orderId = 2;
		Order order = repo.findById(orderId).get();
		
		order.setStatus(OrderStatus.SHIPPING);
		order.setPaymentMethod(PaymentMethod.COD);
		order.setOrderTime(new Date());
		order.setDeliverDays(2);
		
		Order updatedOrder = repo.save(order);
		
		assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
	}
	
	@Test
	public void testGet() {
		Integer orderId = 2;
		Order order = repo.findById(orderId).get();
		
		assertThat(order).isNotNull();
		System.out.println(order);
	}
	
	@Test
	public void testDelete() {
		Integer orderId = 3;
		
		repo.deleteById(orderId);
		
		Optional<Order> optional = repo.findById(orderId);
		
		assertThat(optional).isNotPresent();
	}
}
