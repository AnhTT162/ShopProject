package com.shop.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.admin.setting.country.CountryRepository;
import com.shop.common.entity.Country;
import com.shop.common.entity.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StatesRestControllerTests {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	CountryRepository countryRepository;
	@Autowired
	StateRepository stateRepository;

	@Test
	@WithMockUser(username = "anh01652851901@gmail.com", password = "Trang191919", roles = "Quản lý")
	public void testListByCountry() throws Exception {
		Integer countryId = 3;
		String url = "/states/list_by_country/" + countryId;

		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();
		State[] states = objectMapper.readValue(jsonResponse, State[].class);

		assertThat(states).hasSizeGreaterThan(1);

	}

	@Test
	@WithMockUser(username = "anh01652851901@gmail.com", password = "Trang191919", roles = "Quản lý")
	public void testSaveState() throws Exception {
		String url = "/states/save";
		Integer countryId = 3;
		Country country = countryRepository.findById(countryId).get();
		State state = new State("Kaido", country);

		MvcResult result = mockMvc.perform(
				post(url).contentType("application/json").content(objectMapper.writeValueAsString(state)).with(csrf()))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		Integer stateId = Integer.parseInt(response);
		Optional<State> findById = stateRepository.findById(stateId);
		assertThat(findById.isPresent());
	}

	@Test
	@WithMockUser(username = "anh01652851901@gmail.com", password = "Trang191919", roles = "Quản lý")
	public void testUpdateCountry() throws Exception {
		String url = "/states/save";
		Integer stateId = 6;
		String stateName = "Kaizo";
		State state = stateRepository.findById(stateId).get();
		state.setName(stateName);
		
		mockMvc.perform(post(url).contentType("application/json").content(objectMapper.writeValueAsString(state))
				.with(csrf())).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(String.valueOf(stateId)));

		Optional<State> findById = stateRepository.findById(stateId);
		assertThat(findById).isPresent();
		State updatedState = findById.get();

		assertThat(updatedState.getName()).isEqualTo(stateName);
	}

	@Test
	@WithMockUser(username = "anh01652851901@gmail.com", password = "Trang191919", roles = "Quản lý")
	public void testDeleteCountry() throws Exception {
		Integer stateId = 6;
		String url = "/states/delete/" + stateId;
		mockMvc.perform(get(url)).andExpect(status().isOk());

		Optional<State> findById = stateRepository.findById(stateId);

		assertThat(findById).isNotPresent();
	}

}
