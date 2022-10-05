var buttonLoad4State;
var dropDownCountry4State;
var dropDownStates;
var labelStateName;
var fieldStateName;
var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;

$(document).ready(function() {
	buttonLoad4State = $("#buttonLoadCountriesForStates");
	dropDownCountry4State = $("#dropDownCountriesForStates");
	dropDownStates = $("#dropDownStates");
	labelStateName = $("#labelStateName");
	fieldStateName = $("#fieldStateName");
	buttonAddState = $("#buttonAddState");
	buttonUpdateState = $("#buttonUpdateState");
	buttonDeleteState = $("#buttonDeleteState");

	buttonLoad4State.click(function() {
			loadCountries4State();
			noSelectState();
	});
	dropDownCountry4State.on("change", function() {
		changeDropDownStates();
		noSelectState();
	});
	dropDownStates.on("change", function() {
		changeFormStateToSelectedState();
	});
	buttonAddState.click(function() {
		if (buttonAddState.val() == "Lưu") {
			addState();
		} else {
			changeFormStateToNew();
		}
	});
	buttonUpdateState.click(function() {
		updateState();
	});
	buttonDeleteState.click(function() {
		deleteState();
	});

});

function deleteState() {
	stateId = dropDownStates.val();
	url = contextPath + "states/delete/" + stateId;
	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}	
	}).done(function() {
		$("#dropDownStates option[value='" + stateId + "']").remove();
		noSelectState();
		showToastMessage("Thông tin thành phố đã được xóa.");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
}

function updateState() {
	url = contextPath + "states/save";
	stateName = fieldStateName.val();
	stateId = dropDownStates.val();

	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();

	jsonData = { id: stateId, name: stateName, country: { id: countryId } };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId) {
		$("#dropDownStates option:selected").val(stateId);
		$("#dropDownStates option:selected").text(stateName);
		showToastMessage("Thông tin thành phố đã được cập nhật.");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
}

function addState() {
	url = contextPath + "states/save";
	stateName = fieldStateName.val();

	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();

	jsonData = { name: stateName, country: { id: countryId } };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId) {
		selectNewlyAddedState(stateId, stateName);
		showToastMessage("Thành phố mới đã được thêm.");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
}

function selectNewlyAddedState(stateId, stateName) {
	$("<option>").val(stateId).text(stateName).appendTo(dropDownStates);
	$("#dropDownStates option[value='" + stateId + "']").prop("selected", true);
	fieldStateName.val(stateName).focus();
	buttonAddState.prop("value", "Thêm mới");
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);
	labelStateName.text("Tên Tình thành/Bang (đang chọn): ")
}

function changeFormStateToNew() {
	buttonAddState.val("Lưu");
	labelStateName.text("Tên thành phố: ");
	buttonUpdateState.prop("disabled", true);
	buttonDeleteState.prop("disabled", true);

	fieldStateName.val("").prop("disabled", false).focus();
}


function changeFormStateToSelectedState() {
	buttonAddState.prop("value", "Thêm mới");
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);

	labelStateName.text("Tên thành phố (đang chọn): ")
	selectedStateName = $("#dropDownStates option:selected").text();
	fieldStateName.val(selectedStateName).prop("disabled", false).focus();

}

function changeDropDownStates() {
	selectedCountry = $("#dropDownCountriesForStates option:selected")
	countryId = selectedCountry.val();
	url = contextPath + "states/list_by_country/" + countryId;
	$.get(url, function(responseJSON) {
		dropDownStates.empty();

		$.each(responseJSON, function(index, state) {
			optionValue = state.id;
			$("<option>").val(optionValue).text(state.name).appendTo(dropDownStates);
		});
	}).done(function() {
		showToastMessage("Tất cả thành phố đã được hiển thị!");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
	fieldStateName.val(null);
}

function loadCountries4State() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropDownCountry4State.empty();

		$.each(responseJSON, function(index, country) {
			$("<option>").val(country.id).text(country.name).appendTo(dropDownCountry4State);
		});
	}).done(function() {
		buttonLoad4State.val("Làm mới danh sách quốc gia");
		dropDownStates.empty();
		showToastMessage("Tất cả quốc gia đã được hiển thị!");
	}).fail(function() {
		showToastMessage("Lỗi: Không thể kết nối đến máy chủ hoặc máy chủ gặp lỗi.");
	});
}

function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}
function noSelectState() {
	buttonAddState.val("Thêm mới").prop("disabled", false);
	buttonUpdateState.prop("disabled", true);
	buttonDeleteState.prop("disabled", true);
	fieldStateName.val("").prop("disabled", true);
	labelStateName.text("Tên thành phố: ");
}