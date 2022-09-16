package com.shop.admin.product;

import java.io.IOException;
import java.util.List;

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

@Controller
public class ProductController {

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

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Thêm sản phẩm mới");

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes attributes,
			@RequestParam("fileImage") MultipartFile mainImageMultipartFile,
			@RequestParam("extraImage") MultipartFile[] extraImageMultipartFiles) throws IOException {
		setMainImageName(mainImageMultipartFile, product);
		setExtraImageNames(extraImageMultipartFiles, product);

		Product savedProduct = productService.save(product);

		saveUploadedImages(mainImageMultipartFile, extraImageMultipartFiles, savedProduct);

		attributes.addFlashAttribute("message", "Sản phẩm mới đã được lưu");

		return "redirect:/products";
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

	private void setExtraImageNames(MultipartFile[] extraImageMultipartFiles, Product product) {
		if (extraImageMultipartFiles.length > 0) {
			for (MultipartFile file : extraImageMultipartFiles) {
				if (!file.isEmpty()) {
					String fileName = StringUtils.cleanPath(file.getOriginalFilename().replaceAll(" ", "-"));
					product.addExtraImage(fileName);
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

}
