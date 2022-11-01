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