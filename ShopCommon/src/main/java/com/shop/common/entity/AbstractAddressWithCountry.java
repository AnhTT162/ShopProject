package com.shop.common.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractAddressWithCountry extends AbstractAddress{
	@ManyToOne
	@JoinColumn(name = "country_id")
	protected Country country;
	
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
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
