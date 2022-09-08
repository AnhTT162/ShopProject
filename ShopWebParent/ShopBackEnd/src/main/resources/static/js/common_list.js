function clearFilter() {
	window.location = moduleURL;
}

function showDeleteConfirmModal(link, entityName) {

	entityId = link.attr("entityId");
	$("#yesButton").attr("href",
		link.attr("href"));
	$("#confirmText").text(
		"Bạn có chắc chắn muốn xóa " + entityName + " này (ID: "
		+ entityId + ")?");
	$("#confirmModal").modal();
}