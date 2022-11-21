package com.shop.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "address")
public class Address extends AbstractAddressWithCountry{
	
	

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(name = "default_address")
	private boolean defaultForShipping;

	public Address() {
		// TODO Auto-generated constructor stub
	}







	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean isDefaultForShipping() {
		return defaultForShipping;
	}

	public void setDefaultForShipping(boolean defaultForShipping) {
		this.defaultForShipping = defaultForShipping;
	}

	@Override
	public String toString() {
		String receiveAddress = firstName;

		if (lastName != null && !lastName.isEmpty())
			receiveAddress += " " + lastName;

		if (!addressDetail.isEmpty())
			receiveAddress += ", " + addressDetail;

		if (!city.isEmpty())
			receiveAddress += ", " + city;

		if (state != null && !state.isEmpty())
			receiveAddress += ", " + state;

		receiveAddress += ", " + country.getName();

		if (!postalCode.isEmpty())
			receiveAddress += ". Mã bưu điện: " + postalCode;

		if (!phoneNumber.isEmpty())
			receiveAddress += ". Số điện thoại: " + phoneNumber;

		return receiveAddress;
	}

}
