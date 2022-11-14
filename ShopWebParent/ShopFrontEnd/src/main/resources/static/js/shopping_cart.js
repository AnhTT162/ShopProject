decimalSeparator = decimalPointType == 'COMMA' ? ',' : '.';
thousandsSeparator = thousandsPointType == 'COMMA' ? ',' : '.';
$(document).ready(function(){
	$(".linkMinus").on("click", function(evt){
		evt.preventDefault();
		decreaseQuantity($(this));

	});
	$(".linkPlus").on("click", function(evt){
		evt.preventDefault();
		increaseQuantity($(this));
	});
	$(".linkRemove").on("click", function(evt){
		evt.preventDefault();
		removeProduct($(this));
	});
});

function decreaseQuantity(link){
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) - 1;
	
	if(newQuantity > 0) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		showWarningModal('Số lượng tối thiểu là 1')
	}
};

function increaseQuantity(link){
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) + 1;
	
	if(newQuantity < 11) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		showWarningModal('Số lượng tối đa là 10')
	}
};

function updateQuantity(productId, quantity){
	url = contextPath + "cart/update/" + productId + "/" + quantity;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(updatedSubTotal){
		updateSubTotal(updatedSubTotal, productId);
		updateTotal();
	}).fail(function(){
		showErrorModal("Đã xảy ra lỗi khi cập nhật số lượng sản phẩm.")
	});
};

function updateSubTotal(updatedSubTotal, productId){
	$("#subTotal" + productId).text(formatCurrency(updatedSubTotal));
};

function updateTotal(){
	total = 0.0;
	productCount = 0;
	$(".subtotal").each(function(index, element){
		productCount++;
		total += parseFloat(clearCurrencyFormat(element.innerHTML));
	});
	
	if(productCount < 1){
		showEmptyShoppingCart();
	} else {
		$("#total").text(formatCurrency(total));
	}
	
};

function showEmptyShoppingCart(){
	$("#sectionTotal").hide();
	$("#sectionEmptyCartMessage").removeClass("d-none")
};

function removeProduct(link){
	url = link.attr("href");
	
	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response){
		rowNumber = link.attr("rowNumber");
		removeProductHTML(rowNumber);
		updateTotal();
		updateCountNumber();
		showModalDialog("Giỏ hàng", response);
	}).fail(function(){
		showErrorModal("Đã xảy ra lỗi khi xóa sản phẩm.")
	});
}

function removeProductHTML(rowNumber){
	$("#row" + rowNumber).remove();
	$("#blankLine" + rowNumber).remove();
}

function updateCountNumber(){
	$(".divCount").each(function(index, element){
		element.innerHTML = "" + (index + 1);
	});
};

function formatCurrency(amount){
	return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
};

function clearCurrencyFormat(numberString){
	result = numberString.replaceAll(thousandsSeparator, "");
	return result.replaceAll(decimalSeparator, ".");
};