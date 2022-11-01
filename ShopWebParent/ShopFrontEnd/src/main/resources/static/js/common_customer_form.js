/**
 * 
 */
var dropDownCountry;
var dataListStates;
var fieldState;
$(document).ready(function() {
	dropDownCountry = $("#country");
	dataListStates = $("#listStates");
	fieldState = $("#state");
	dropDownCountry.on("change", function() {
		loadStatesByCountry();
		fieldState.val("").focus();
	});
});
function loadStatesByCountry() {
	selectedCountry = $("#country option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "settings/list_states_by_country/" + countryId;

	$.get(url, function(responseJSON) {
		dataListStates.empty();

		$.each(responseJSON, function(index, state) {
			$("<option>").val(state.name).text(state.name).appendTo(dataListStates);
		})
	});
};
function checkPasswordMatch(confirmPassword) {
	if (confirmPassword.value != $("#password").val()) {
		confirmPassword.setCustomValidity("Mật khẩu không trùng khớp");
	} else {
		confirmPassword.setCustomValidity("");
	}
};
function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();

}

function showErrorModal(message) {
	showModalDialog("Lỗi", message);
}
function showWarningModal(message) {
	showModalDialog("Cảnh báo", message);
}