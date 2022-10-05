var buttonLoad;
var dropDownCountry;
var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;
var labelCountryName;
var labelCountryCode;
var fieldCountryName;
var fieldCountryCode;

$(document).ready(function() {
	buttonLoad = $("#buttonLoadCountries");
	dropDownCountry = $("#dropDownCountries");
	buttonAddCountry = $("#buttonAddCountry");
	buttonUpdateCountry = $("#buttonUpdateCountry");
	buttonDeleteCountry = $("#buttonDeleteCountry");
	labelCountryName = $("#labelCountryName");
	fieldCountryName = $("#fieldCountryName");
	labelCountryCode = $("#labelCountryCode");
	fieldCountryCode = $("#fieldCountryCode");

	buttonLoad.click(function() {
			loadCountries();
			noSelectCountry();
	});
	dropDownCountry.on("change", function() {
		changeFormToSelectedCountry();
	});
	buttonAddCountry.click(function() {
		if (buttonAddCountry.val() == "Lưu") {
			addCountry();
		} else {
			changeFormCountryToNew();
		}
	});
	buttonUpdateCountry.click(function() {
		updateCountry();
	});
	buttonDeleteCountry.click(function() {
		deleteCountry();
	});

});

function deleteCountry() {
	optionValue = dropDownCountry.val();
	countryId = optionValue.split("-")[0];
	url = contextPath + "countries/delete/" + countryId;

	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function() {
		$("#dropDownCountries option[value='" + optionValue + "']").remove();
		noSelectCountry();
		showToastMessage("Thông tin quốc gia đã được xóa.");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
}

function updateCountry() {
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();

	countryId = dropDownCountry.val().split("-")[0];

	jsonData = { id: countryId, name: countryName, code: countryCode };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId) {
		$("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
		$("#dropDownCountries option:selected").text(countryName);
		showToastMessage("Thông tin quốc gia đã được cập nhật.");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
}

function addCountry() {
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();

	jsonData = { name: countryName, code: countryCode };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId) {
		selectNewlyAddedCountry(countryId, countryCode, countryName);
		showToastMessage("Quốc gia mới đã được thêm.");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
}

function selectNewlyAddedCountry(countryId, countryCode, countryName) {
	optionValue = countryId + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry);
	$("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);
	fieldCountryCode.val(countryCode);
	fieldCountryName.val(countryName).focus();
	buttonAddCountry.prop("value", "Thêm mới");
	buttonUpdateCountry.prop("disabled", false);
	buttonDeleteCountry.prop("disabled", false);
	labelCountryName.text("Tên quốc gia (đang chọn): ")
}

function changeFormCountryToNew() {
	buttonAddCountry.val("Lưu");
	labelCountryName.text("Tên quốc gia: ");
	buttonUpdateCountry.prop("disabled", true);
	buttonDeleteCountry.prop("disabled", true);

	fieldCountryCode.val("").prop("disabled", false);
	fieldCountryName.val("").prop("disabled", false).focus();
}


function changeFormToSelectedCountry() {
	buttonAddCountry.prop("value", "Thêm mới");
	buttonUpdateCountry.prop("disabled", false);
	buttonDeleteCountry.prop("disabled", false);
	labelCountryName.text("Tên quốc gia (đang chọn): ")
	selectedCountryName = $("#dropDownCountries option:selected").text();
	fieldCountryName.val(selectedCountryName).prop("disabled", false).focus();
	countryCode = dropDownCountry.val().split("-")[1];
	fieldCountryCode.val(countryCode).prop("disabled", false);

}

function loadCountries() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropDownCountry.empty();

		$.each(responseJSON, function(index, country) {
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
		});
	}).done(function() {
		buttonLoad.val("Làm mới danh sách");
		showToastMessage("Tất cả quốc gia đã được hiển thị!");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
}

function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}

function noSelectCountry(){
	buttonAddCountry.val("Thêm mới");
	labelCountryName.text("Tên quốc gia: ");
	buttonUpdateCountry.prop("disabled", true);
	buttonDeleteCountry.prop("disabled", true);
	fieldCountryCode.val("").prop("disabled", true);
	fieldCountryName.val("").prop("disabled", true);
}