package com.shop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	
	@Autowired
	private RoleRepository repo;
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Quản lý", "Quản lý toàn bộ hệ thống");
		Role savedRole = repo.save(roleAdmin);
		assertThat(savedRole.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateRestRoles() {
		Role roleSalesperson = new Role("Nhân viên bán hàng", "Quản lý sản phẩm, "
				+ "khách hàng, giao hàng, hóa đơn, thống kê");
		Role roleEditor = new Role("Nhân viên kho", "Quản lý danh mục, "
				+ "sản phẩm, bài viết, menu");
		Role roleShipper = new Role("Nhân viên giao hàng", "Xem sản phẩm, "
				+ "xem hóa đơn, cập nhật trạng thái hóa đơn");
		Role roleAssitant = new Role("Nhân viên chăm sóc khách hàng", "Quản lý câu hỏi và đánh giá");
		
		repo.saveAll(List.of(roleSalesperson,roleEditor,roleShipper,roleAssitant));
	}

}
