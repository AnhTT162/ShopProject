$(document)
	.ready(
		function() {
			$("#buttonCancel").on("click", function() {
				window.location = moduleURL;
			});

			$("#fileImage")
				.change(
					function() {
						if(!checkFileSize(this)){
							return;
						}
							showImageThumbnail(this);
						
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

function checkFileSize(fileInput){
	fileSize = fileInput.files[0].size;
						if (fileSize > MAX_FILE_SIZE) {
							fileInput
								.setCustomValidity("Kích thước ảnh tải lên phải nhỏ hơn " + MAX_FILE_SIZE/1024 + " KB!");
							fileInput.reportValidity();
							return false;
						} else {
							fileInput.setCustomValidity("");
							return true;
						}
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
