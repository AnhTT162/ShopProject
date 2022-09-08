package com.shop.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Thời trang");
		Category savedCategory = repo.save(category);
		assertThat(savedCategory.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(5);
		Category man = new Category("Giày dép nam", parent);
		repo.saveAll(List.of(man));
		
	}
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(2).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildren();
		
		for(Category subCategory : children) {
			System.out.println(subCategory.getName());
		}
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> categories = repo.findAll();
		
		for (Category category : categories) {
			if(category.getParent() == null) {
				System.out.println(category.getName());
				
				Set<Category> children = category.getChildren();
				
				for(Category subCatagory : children) {
					System.out.println("--" + subCatagory.getName());
					printChildren(subCatagory, 1);
				}
			}
		}
		
	}
	private void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		for(Category subCategory : children) {
			for(int i = 0; i < newSubLevel; i++) {
				System.out.print("--");
			}
			System.out.println(subCategory.getName());
			printChildren(subCategory, newSubLevel);
		}
		
	}
	
	@Test
	public void testListRootCategories() {
		List<Category> listRootCategories = repo.findRootCategories(Sort.by("name").ascending());
		listRootCategories.forEach(cat -> System.out.println(cat.getName()));
	}
	
	@Test
	public void testFindByName() {
		String name = "Máy tính";
		Category category = repo.findByName(name);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
	
	@Test
	public void testFindByAlias() {
		String alias = "books";
		Category category = repo.findByAlias(alias);
		assertThat(category).isNotNull();
		assertThat(category.getAlias()).isEqualTo(alias);
	}

}
