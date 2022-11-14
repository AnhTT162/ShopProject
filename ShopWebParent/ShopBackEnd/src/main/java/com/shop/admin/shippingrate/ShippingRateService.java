package com.shop.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shop.admin.setting.country.CountryRepository;
import com.shop.common.entity.Country;
import com.shop.common.entity.ShippingRate;

@Service
@Transactional
public class ShippingRateService {

	public static final int RATES_PER_PAGE = 10;
	
	@Autowired private ShippingRateRepository shipRepo;
	@Autowired private CountryRepository countryRepo;
	
	public Page<ShippingRate> findAll(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
 		Pageable pageable = PageRequest.of(pageNum - 1, RATES_PER_PAGE, sort);
 		if(keyword != null) {
 			return shipRepo.findAll(keyword, pageable);
 		}
 		return shipRepo.findAll(pageable);
	}
	
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
		ShippingRate rateInDB = shipRepo.findByCountryAndState(rateInForm.getCountry().getId(), rateInForm.getState());
		
		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);
		
		if(foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
			throw new ShippingRateAlreadyExistsException("Địa chỉ giao hàng " + rateInForm.getState() + ", " + rateInForm.getCountry().getName() + " đã tồn tại.");
		}
		shipRepo.save(rateInForm);
	}
	
	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		try {
			return shipRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("Không tồn tại địa chỉ có ID " + id);
		}
	}
	
	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		Long countById = shipRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new ShippingRateNotFoundException("Không tồn tại địa chỉ có ID " + id);
		}
		shipRepo.updateCODSupport(id, codSupported);
	}
	
	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long countById = shipRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new ShippingRateNotFoundException("Không tồn tại địa chỉ có ID " + id);
		}
		shipRepo.deleteById(id);
	}
}
