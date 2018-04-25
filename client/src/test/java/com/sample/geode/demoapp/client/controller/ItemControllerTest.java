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

import com.sample.geode.demoapp.model.Item;
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


public class ItemControllerTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private Region<String, Item> mockRegion;

	private MockMvc mockMvc;

	@Captor
	private ArgumentCaptor<String> keyCaptor;
	@Captor
	private ArgumentCaptor<Item> itemCaptor;

	@Before
	public void setUp() {
		initMocks(this);
		ItemController itemController = new ItemController(mockRegion);
		mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
	}

	@Test
	public void retrieve() throws Exception {
		Item item = new Item("some-name", "some-description", "some-price");

		when(mockRegion.get(any())).thenReturn(item);

		MvcResult mvcResult = mockMvc.perform(get("/item/some-key")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		assertThatJson(responseBody).node("name").isEqualTo(item.getName());
		assertThatJson(responseBody).node("description").isEqualTo(item.getDescription());
		assertThatJson(responseBody).node("price").isEqualTo(item.getPrice());

		verify(mockRegion).get("some-key");
	}

	@Test
	public void store() throws Exception {
		Item item = new Item("some-name", "some-description", "some-price");
		String requestBody = objectMapper.writeValueAsString(item);

		mockMvc.perform(post("/item/some-key").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isNoContent());

		verify(mockRegion).put(keyCaptor.capture(), itemCaptor.capture());
		assertThat(keyCaptor.getValue()).isEqualTo("some-key");
		assertThat(itemCaptor.getValue()).isEqualToComparingFieldByField(item);
	}

}
