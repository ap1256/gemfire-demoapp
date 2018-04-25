package com.sample.geode.demoapp.client.config;

import static org.apache.geode.cache.client.ClientRegionShortcut.LOCAL;
import static org.apache.geode.cache.client.ClientRegionShortcut.PROXY;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.sample.geode.demoapp.client.config.GemfireProperties.LocatorProperties;
import com.sample.geode.demoapp.model.Customer;
import com.sample.geode.demoapp.model.CustomerOrder;
import com.sample.geode.demoapp.model.Item;

@Configuration
@EnableConfigurationProperties(GemfireProperties.class)
public class GemfireConfig {
	private static final Logger logger = LoggerFactory.getLogger(GemfireConfig.class);

	@Autowired
	private GemfireProperties gemfireProperties;

	@Bean
	public ClientCache cache() {
		logger.info("cache: locators={}", gemfireProperties.getLocators().toString());
		if (gemfireProperties.getLocators().size() > 0) {
			setupStatica();
		}

		ClientCacheFactory ccf = new ClientCacheFactory();

		ccf.setPdxPersistent(false);
		ccf.setPdxReadSerialized(false);
		ccf.setPdxSerializer(new ReflectionBasedAutoSerializer("com.sample.geode.demoapp.model.*"));

		addLocators(ccf, gemfireProperties);

		return ccf.create();
	}

	@Bean
	@Scope(value = SCOPE_PROTOTYPE)
	ClientRegionFactory<?, ?> clientRegionFactory(final ClientCache cache) {
		ClientRegionShortcut shortcut = (gemfireProperties.getLocators().size() > 0) ? PROXY : LOCAL;
		return cache.createClientRegionFactory(shortcut);
	}

	@Bean(name = "customer", destroyMethod = "")
	Region<String, Customer> customerRegion(final ClientRegionFactory<String, Customer> clientRegionFactory) {
		logger.info("customer");
		return clientRegionFactory.create("customer");
	}

	@Bean(name = "customer-order", destroyMethod = "")
	Region<String, CustomerOrder> customerOrderRegion(
			final ClientRegionFactory<String, CustomerOrder> clientRegionFactory) {
		logger.info("customer-order");
		return clientRegionFactory.create("customer-order");
	}

	@Bean(name = "item", destroyMethod = "")
	Region<String, Item> itemRegion(final ClientRegionFactory<String, Item> clientRegionFactory) {
		logger.info("item");
		return clientRegionFactory.create("item");
	}

	private static void addLocators(final ClientCacheFactory cacheFactory,
			GemfireProperties clientApplicationProperties) {
		logger.info("addLocators: locators={}", clientApplicationProperties);
		for (LocatorProperties locator : clientApplicationProperties.getLocators()) {
			cacheFactory.addPoolLocator(locator.getHost(), locator.getPort());
		}
	}

	private static void setupStatica() {
		logger.info("setupStatica");
		if (System.getenv().containsKey("VCAP_SERVICES")) {
			;
			// To get VCAP_SERVICES
			String vcapServices = System.getenv("VCAP_SERVICES");

			// To get the JSON object
			JSONObject vcapServicesJSON = new JSONObject(vcapServices);

			try {
				// To get the Statica url
				String staticaUrl = vcapServicesJSON.getJSONArray("statica").getJSONObject(0)
						.getJSONObject("credentials").getString("STATICA_URL");

				// To get rid of the "https:// string at the beginning
				staticaUrl = staticaUrl.substring(7);

				// To split the url using @ as separator in order to get the
				// credentials
				// in one string (result[0])
				// and host and port in other (result[1])
				String[] result = staticaUrl.split("@");

				// To get the credentials strings consisting of 2 strings, user
				// and
				// password,
				// using : as separator
				String[] credentials = result[0].split(":");

				// To get host and port, using : as separator
				String[] hostAndPort = result[1].split(":");

				// To get the Statica User
				String staticaUser = credentials[0];

				// To get the Statica pass
				String staticaPass = credentials[1];

				// To get the statica host
				String staticaHost = hostAndPort[0];

				// To get the statica port
				String staticaPort = "1080";

				Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						PasswordAuthentication p = new PasswordAuthentication(staticaUser, staticaPass.toCharArray());
						return p;
					}
				});

				System.setProperty("socksProxyHost", staticaHost);
				System.setProperty("socksProxyPort", staticaPort);
			} catch (Exception ex) {
				logger.error("error setting up statica!", ex);
			}
		}
	}
}
