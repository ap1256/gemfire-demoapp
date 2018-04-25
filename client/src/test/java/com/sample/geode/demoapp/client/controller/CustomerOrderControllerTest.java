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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sample.geode.demoapp.model.CustomerOrder;
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


public class CustomerOrderControllerTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private Region<String, CustomerOrder> mockRegion;

	private MockMvc mockMvc;

	@Captor
	private ArgumentCaptor<String> keyCaptor;
	@Captor
	private ArgumentCaptor<CustomerOrder> customerOrderCaptor;

	@Before
	public void setUp() {
		initMocks(this);
		CustomerOrderController customerOrderController = new CustomerOrderController(mockRegion);
		mockMvc = MockMvcBuilders.standaloneSetup(customerOrderController).build();
	}

	@Test
	public void retrieve() throws Exception {

		String[] itemKeys = { "i1" };
		Set<String> itemSet = new HashSet<String>(Arrays.asList(itemKeys));
		CustomerOrder customerOrder = new CustomerOrder("c1", "some-address", (new Date()).getTime(), itemSet);

		when(mockRegion.get(any())).thenReturn(customerOrder);

		MvcResult mvcResult = mockMvc.perform(get("/customer-order/some-key")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		assertThatJson(responseBody).node("customerId").isEqualTo(customerOrder.getCustomerId());
		assertThatJson(responseBody).node("shippingAddress").isEqualTo(customerOrder.getShippingAddress());
		assertThatJson(responseBody).node("orderDate").isEqualTo(customerOrder.getOrderDate());
		assertThatJson(responseBody).node("itemSet").isEqualTo(customerOrder.getItemSet());

		verify(mockRegion).get("some-key");
	}

	@Test
	public void store() throws Exception {
		String[] itemKeys = { "i1" };
		Set<String> itemSet = new HashSet<String>(Arrays.asList(itemKeys));
		CustomerOrder customerOrder = new CustomerOrder("c1", "some-address", (new Date()).getTime(), itemSet);
		String requestBody = objectMapper.writeValueAsString(customerOrder);

		mockMvc.perform(post("/customer-order/some-key").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isNoContent());

		verify(mockRegion).put(keyCaptor.capture(), customerOrderCaptor.capture());
		assertThat(keyCaptor.getValue()).isEqualTo("some-key");
		assertThat(customerOrderCaptor.getValue()).isEqualToComparingFieldByField(customerOrder);
	}

}
