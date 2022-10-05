package com.shop.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Country;
import com.shop.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {
	@Autowired private StateRepository repo;
	@Autowired private TestEntityManager entityManager;
	
	@Test
	public void testCreateStateInChina() {
		Integer countryId = 3;
		Country country = entityManager.find(Country.class, countryId);
		
		State state = repo.save(new State("Kyoto", country));
		
		assertThat(state).isNotNull();
		assertThat(state.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListStateByCountry() {
		Integer countryId = 1;
		Country country = entityManager.find(Country.class, countryId);
		List<State> listStates = repo.findByCountryOrderByNameAsc(country);
		
		listStates.forEach(System.out::println);
		
		assertThat(listStates.size()).isGreaterThan(0);
	}
	
	@Test
	public void testUpdateState() {
		Integer stateId = 1;
		String name = "Kyoto1";
		
		State state = repo.findById(stateId).get();
		
		state.setName(name);
		
		State updatedState = repo.save(state);
		
		assertThat(updatedState.getName()).isEqualTo(name);
	}
	
	@Test
	public void testGetState() {
		Integer id = 2;
		State state = repo.findById(id).get();
		
		assertThat(state).isNotNull();
	}
	
	@Test
	public void testDeleteState() {
		Integer id = 2;
		repo.deleteById(id);
		
		Optional<State> findById = repo.findById(id);
		
		assertThat(findById).isEmpty();
	}
}
