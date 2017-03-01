package com.github.charleslzq.loghub.binder;

import com.github.charleslzq.loghub.binder.config.LogHubExtendedBindingProperties;
import com.github.charleslzq.loghub.binder.extract.SimpleSpansExtractor;
import com.github.charleslzq.loghub.binder.extract.SpansExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
	@ConditionalOnMissingBean
	public SpansExtractor spansExtractor() {
		return new SimpleSpansExtractor();
	}

	@Bean
	public LogHubMessageChannelBinder logHubMessageChannelBinder(SpansExtractor spansExtractor) {
		return new LogHubMessageChannelBinder(logHubExtendedBindingProperties, spansExtractor);
	}
}
