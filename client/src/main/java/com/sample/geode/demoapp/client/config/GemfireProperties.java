package com.sample.geode.demoapp.client.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("gemfire")
@Validated
public class GemfireProperties {

	private List<LocatorProperties> locators = new ArrayList<>();

	public List<LocatorProperties> getLocators() {
		return locators;
	}

	public void setLocators(List<LocatorProperties> locators) {
		this.locators = locators;
	}

	@Override
	public String toString() {
		return "GemfireProperties [locators=" + locators + "]";
	}

	public static class LocatorProperties {
		private String host = "localhost";
		private Integer port = 10334;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		@Override
		public String toString() {
			return "LocatorProperties [host=" + host + ", port=" + port + "]";
		}
	}
}
