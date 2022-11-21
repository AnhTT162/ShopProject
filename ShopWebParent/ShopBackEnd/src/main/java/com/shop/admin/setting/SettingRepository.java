package com.shop.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shop.common.entity.setting.Setting;
import com.shop.common.entity.setting.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {

	public List<Setting> findByCategory(SettingCategory category);
}
