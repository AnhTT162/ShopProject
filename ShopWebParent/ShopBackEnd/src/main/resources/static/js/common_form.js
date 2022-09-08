$(document)
	.ready(
		function() {
			$("#buttonCancel").on("click", function() {
				window.location = moduleURL;
			});

			$("#fileImage")
				.change(
					function() {
						fileSize = this.files[0].size;
						if (fileSize > 1048576) {
							this
								.setCustomValidity("Kích thước ảnh tải lên phải nhỏ hơn 1MB!");
							this.reportValidity();
						} else {
							this.setCustomValidity("");
							showImageThumbnail(this);
						}
					});
		});

function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
}

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
