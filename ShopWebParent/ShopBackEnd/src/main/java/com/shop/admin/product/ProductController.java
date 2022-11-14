package com.shop.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
import com.shop.admin.category.CategoryService;
import com.shop.common.entity.Brand;
import com.shop.common.entity.Category;
import com.shop.common.entity.Product;
import com.shop.common.entity.ProductImage;
import com.shop.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, "name", "asc", null, 0, model);
	}
	@GetMapping("products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			String sortField, String sortDir, String keyword, Integer categoryId,
			Model model) {
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		long startCount = (pageNum -1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		if(categoryId != null) model.addAttribute("categoryId", categoryId);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listCategories", listCategories);
		
		
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

	@GetMapping("/products/{id}/enabled/{status}/{pageNum}")
	public String updateProductEnabledStatus(@PathVariable(name = "pageNum") int pageNum,@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status,
			String sortField, String sortDir, String keyword, @Param("categoryId") Integer categoryId, Model model) {
		productService.updateProductEnabledStatus(id, status);
		String proStatus = status ? " đã được kích hoạt" : " đã bị vô hiệu hóa";
		String message = "Sản phẩm có ID: " + id + proStatus; 
		model.addAttribute("message", message);	
		return listByPage(pageNum, sortField, sortDir, keyword, categoryId, model);
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
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Product product = productService.get(id);
			model.addAttribute("product", product);
			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

}
