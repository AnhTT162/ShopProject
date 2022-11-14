$(document).ready(function(){
	$("#buttonAddToCart").on("click", function(evt){
		addToCart();
	});
});

function addToCart(){
	quantity = $("#quantity" + productId).val();
	url = contextPath + "cart/add/" + productId + "/" + quantity;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response){
		showModalDialog("Giỏ hàng", response)
	}).fail(function(){
		showErrorModal("Đã xảy ra lỗi khi thêm sản phẩm vào giỏ hàng.")
	});
}