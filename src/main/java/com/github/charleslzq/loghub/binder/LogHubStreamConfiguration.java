package com.github.charleslzq.loghub.binder;

import com.github.charleslzq.loghub.binder.config.LogHubExtendedBindingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Charles on 3/1/2017.
 */
@Configuration
@EnableConfigurationProperties(LogHubExtendedBindingProperties.class)
public class LogHubStreamConfiguration {
	@Autowired
	private LogHubExtendedBindingProperties logHubExtendedBindingProperties;

	@Bean
	public LogHubMessageChannelBinder logHubMessageChannelBinder() {
		return new LogHubMessageChannelBinder(logHubExtendedBindingProperties);
	}
}
