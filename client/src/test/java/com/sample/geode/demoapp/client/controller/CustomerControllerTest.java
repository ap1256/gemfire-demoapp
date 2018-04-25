package com.sample.geode.demoapp.client.controller;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sample.geode.demoapp.model.Customer;
import org.apache.geode.cache.Region;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomerControllerTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private Region<String, Customer> mockRegion;

	private MockMvc mockMvc;

	@Captor
	private ArgumentCaptor<String> keyCaptor;
	@Captor
	private ArgumentCaptor<Customer> testObjectCaptor;

	@Before
	public void setUp() {
		initMocks(this);
		CustomerController customerController = new CustomerController(mockRegion);
		mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
	}

	@Test
	public void retrieve() throws Exception {
		Customer testObject = new Customer("some-name");

		when(mockRegion.get(any())).thenReturn(testObject);

		MvcResult mvcResult = mockMvc.perform(get("/customer/some-key")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		assertThatJson(responseBody).node("name").isEqualTo(testObject.getName());

		verify(mockRegion).get("some-key");
	}

	@Test
	public void store() throws Exception {
		Customer customer = new Customer("some-name");
		String requestBody = objectMapper.writeValueAsString(customer);

		mockMvc.perform(post("/customer/some-key").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isNoContent());

		verify(mockRegion).put(keyCaptor.capture(), testObjectCaptor.capture());
		assertThat(keyCaptor.getValue()).isEqualTo("some-key");
		assertThat(testObjectCaptor.getValue()).isEqualToComparingFieldByField(customer);
	}

}
