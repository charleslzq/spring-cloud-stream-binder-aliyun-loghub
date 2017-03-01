package com.github.charleslzq.loghub.binder.config;

import com.github.charleslzq.loghub.config.LogHubProjectConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.ExtendedBindingProperties;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Charles on 3/1/2017.
 */
@Data
@ConfigurationProperties("spring.cloud.stream.log-hub")
public class LogHubExtendedBindingProperties
		implements ExtendedBindingProperties<LogHubConsumerConfig, LogHubProducerConfig> {
	private Map<String, LogHubProjectConfig> projects = new HashMap<>();
	private Map<String, LogHubProducerConfig> producers = new HashMap<>();
	private Map<String, LogHubConsumerConfig> consumers = new HashMap<>();
	private LogHubProducerParameter producerParameter = new LogHubProducerParameter();

	@Override
	public LogHubConsumerConfig getExtendedConsumerProperties(String name) {
		Assert.isTrue(consumers.containsKey(name),
				"Can't find config for consumer " + name);
		return consumers.get(name);
	}

	@Override
	public LogHubProducerConfig getExtendedProducerProperties(String name) {
		Assert.isTrue(producers.containsKey(name),
				"Can't find config for consumer " + name);
		return producers.get(name);
	}
}
