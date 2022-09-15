package com.shop.admin.product;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.shop.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

}
