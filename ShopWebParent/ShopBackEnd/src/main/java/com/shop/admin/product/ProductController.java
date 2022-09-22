package com.shop.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shop.admin.FileUploadUtil;
import com.shop.admin.brand.BrandService;
import com.shop.common.entity.Brand;
import com.shop.common.entity.Product;
import com.shop.common.entity.ProductImage;

@Controller
public class ProductController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = productService.listAll();

		model.addAttribute("listProducts", listProducts);

		return "products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		model.addAttribute("numberOfExistingExtraImages", 0);
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Thêm sản phẩm mới");

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes attributes,
			@RequestParam("fileImage") MultipartFile mainImageMultipartFile,
			@RequestParam("extraImage") MultipartFile[] extraImageMultipartFiles,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {
		setMainImageName(mainImageMultipartFile, product);
		setExistingExtraImageNames(imageIDs, imageNames, product);
		setNewExtraImageNames(extraImageMultipartFiles, product);
		setProductDetails(detailIDs, detailNames, detailValues, product);
		Product savedProduct = productService.save(product);

		saveUploadedImages(mainImageMultipartFile, extraImageMultipartFiles, savedProduct);
		
		deleteExtraImagesWereRemovedOnForm(product);

		attributes.addFlashAttribute("message", "Thông tin sản phẩm đã được lưu.");

		return "redirect:/products";
	}

	private void deleteExtraImagesWereRemovedOnForm(Product product) {
		String extraImageDir = "../product-images/" + product.getId() + "/extras";
		Path dirpath = Paths.get(extraImageDir);
		
		try {
			Files.list(dirpath).forEach(file -> {
				String filename = file.toFile().getName();
				if(!product.containsImageName(filename)) {
					try {
						Files.delete(file);
						LOGGER.info("Đã xóa file: " + filename);
					} catch (IOException e) {
						LOGGER.error("Không thể xóa file: " + filename);
					}
				} 
			});
		} catch (IOException e) {
			LOGGER.error("Không thể mở thư mục: " + dirpath);
		}
	}

	private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
		if(imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		for (int count = 0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count].replaceAll(" ", "-");
			images.add(new ProductImage(id, name, product));
		}
		
		product.setImages(images);
	}

	private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
		if(detailNames == null || detailNames.length == 0) return;
		
		for (int i = 0; i < detailNames.length; i++) {
			String name = detailNames[i];
			String value = detailValues[i];
			Integer id = Integer.parseInt(detailIDs[i]);
			
			if(id != 0) {
				product.addProductDetail(id, name, value);
			} else if(!name.isEmpty() && !value.isEmpty()) {
				product.addProductDetail(name, value);
			}
		}
	}

	private void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultipartFiles,
			Product savedProduct) throws IOException {
		if (!mainImageMultipartFile.isEmpty()) {

			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename().replaceAll(" ", "-"));
			String uploadDir = "../product-images/" + savedProduct.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);

		}
		if (extraImageMultipartFiles.length > 0) {
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			for (MultipartFile file : extraImageMultipartFiles) {
				if (file.isEmpty()) continue;
				String fileName = StringUtils.cleanPath(file.getOriginalFilename().replaceAll(" ", "-"));
				FileUploadUtil.saveFile(uploadDir, fileName, file);
			}
		}

	}

	private void setNewExtraImageNames(MultipartFile[] extraImageMultipartFiles, Product product) {
		if (extraImageMultipartFiles.length > 0) {
			for (MultipartFile file : extraImageMultipartFiles) {
				if (!file.isEmpty()) {
					String fileName = StringUtils.cleanPath(file.getOriginalFilename().replaceAll(" ", "-"));
					if(!product.containsImageName(fileName)) {
					product.addExtraImage(fileName);
					}
				}
			}
		}
	}

	private void setMainImageName(MultipartFile mainImageMultipartFile, Product product) {
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename().replaceAll(" ", "-"));
			product.setMainImage(fileName);
		}
	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			@PathVariable("pageNum") Integer pageNum, @PathVariable("sort") String sort,
			RedirectAttributes attributes) {
		productService.updateProductEnabledStatus(id, status);
		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes attributes) {

		try {
			productService.delete(id);
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			FileUploadUtil.removeDir(productExtraImagesDir);
			String productImagesDir = "../product-images/" + id;
			FileUploadUtil.removeDir(productImagesDir);

			attributes.addFlashAttribute("message", "Sản phẩm có ID: " + id + " đã được xóa.");
		} catch (ProductNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			return "products/product_form";
		} catch (ProductNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

}
