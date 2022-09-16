var extraImagesCount = 0;

dropdownBrands = $("#brand");
dropdownCategories = $("#category");
$(document).ready(function() {
	$("#shortDescription").richText();
	$("#fullDescription").richText();

	dropdownBrands.change(function() {
		dropdownCategories.empty();
		getCategories();
	});
	getCategories();

	$("input[name='extraImage']").each(function(index) {
		extraImagesCount++;
		$(this).change(function() {

			showExtraImageThumbnail(this, index);
		});
	});
});

function checkFileSize(fileInput) {
	fileSize = fileInput.files[0].size;
	if (fileSize > MAX_FILE_SIZE) {
		fileInput
			.setCustomValidity("Kích thước ảnh tải lên phải nhỏ hơn " + MAX_FILE_SIZE / 1024 + " KB!");
		fileInput.reportValidity();
		return false;
	} else {
		fileInput.setCustomValidity("");
		return true;
	}
}

function showExtraImageThumbnail(fileInput, index) {
	if (!checkFileSize(fileInput)) {
		return;
	}
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#extraThumbnail" + index).attr("src", e.target.result);
	};
	reader.readAsDataURL(file);

	if (index >= extraImagesCount - 1) {
		addNextExtraImageSection(index + 1);
	}
}

function addNextExtraImageSection(index) {
	htmlExtraImage = `
	<div class="col border m-3 p-2" id="divExtraImage${index}">
				<div id="extraImageHeader${index}"><label>Hình ảnh phụ #${index + 1}:</label></div>
				<div class="m-2">
					<img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid"
					src="${defaultImageThumbnailSrc}"/>
				</div>
				<div>
					<input type="file" name="extraImage"
					onchange="showExtraImageThumbnail(this, ${index})"
					accept="image/png, image/jpeg" />
				</div>
			</div>
			`;

	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-black float-right" 
		href="javascript:removeExtraImage(${index - 1})"
		title="Xóa ảnh này."></a>
	`;

	$("#divProductImages").append(htmlExtraImage);

	$("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);

	extraImagesCount++;
}

function removeExtraImage(index) {
	$("#divExtraImage" + index).remove();
}

function getCategories() {
	brandId = dropdownBrands.val();
	url = brandModuleURL + "/" + brandId + "/categories";

	$.get(url, function(responseJson) {
		$.each(responseJson, function(index, category) {
			$("<option>").val(category.id).text(category.name)
				.appendTo(dropdownCategories);
		});
	});
}

function checkUnique(form) {
	productId = $("#id").val();
	productName = $("#name").val();
	csrfValue = $("input[name='_csrf']").val();
	params = { id: productId, name: productName, _csrf: csrfValue };
	$.post(checkUniqueUrl, params, function(response) {
		if (response == "OK") {
			form.submit();
		} else if (response == "Duplicate") {
			showWarningModal("Sản phẩm: \"" + productName + "\" đã tồn tại")
		} else {
			showErrorModal("Phản hồi không xác định từ máy chủ")
		}
	}).fail(function() {
		showErrorModal("Không thể kết nối tới máy chủ");
	});
	return false;
}