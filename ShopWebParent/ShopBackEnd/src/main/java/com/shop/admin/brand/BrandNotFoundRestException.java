package com.shop.admin.brand;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Danh mục không tồn tại")
public class BrandNotFoundRestException extends Exception {

}
