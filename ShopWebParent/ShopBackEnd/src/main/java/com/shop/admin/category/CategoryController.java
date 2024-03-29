package com.shop.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shop.common.entity.Category;
import com.shop.common.exception.CategoryNotFoundException;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(String sortDir,Model model) {
		return listByPage(1, sortDir, null, model);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			String sortDir,String keyword, Model model) {
		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc" ;
		}
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = service.listByPage(pageInfo,pageNum, sortDir, keyword);
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNum -1) * CategoryService.ROOT_CATEGORY_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORY_PER_PAGE - 1;
		if(endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("reverseSortDir",reverseSortDir);
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = service.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Tạo danh mục mới");
		
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category,
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
		if(!multipartFile.isEmpty()) {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename().replaceAll(" ", "-"));
		category.setImage(fileName);
		Category savedCategory = service.save(category);
		String uploadDir = "../category-images/" + savedCategory.getId();
		FileUploadUtil.cleanDir(uploadDir);
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if(category.getImage().isEmpty()) category.setImage(null);
			service.save(category);
		}
		redirectAttributes.addFlashAttribute("message", "Danh mục đã được lưu");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Category category = service.get(id);
			List<Category> listCategories = service.listCategoriesUsedInForm();
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Chỉnh sửa danh mục (ID: " + id + ")");
			return "categories/category_form";
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		}
	}
	@GetMapping("/categories/{id}/enabled/{status}/{pageNum}")
	public String updateCategoryEnabledStatus(@PathVariable(name = "pageNum") int pageNum,@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status,
			String sortField, String sortDir, String keyword,
			Model model) {
		service.updateCategoryEnabledStatus(id, status);
		String catStatus = status ? " đã được kích hoạt" : " đã bị vô hiệu hóa";
		String message = "Danh mục có ID: " + id + catStatus; 
		model.addAttribute("message", message);	
		return listByPage(pageNum, sortDir, keyword, model);
		
	}
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			redirectAttributes.addFlashAttribute("message", "Danh mục ID " + id + " đã được xóa");
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/categories";
	}
}
