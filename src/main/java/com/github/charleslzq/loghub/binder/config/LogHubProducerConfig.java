package com.github.charleslzq.loghub.binder.config;

import com.github.charleslzq.loghub.config.SourceType;
import com.github.charleslzq.loghub.converter.DefaultLogItemConverter;
import lombok.Data;

/**
 * Created by Charles on 3/1/2017.
 */
@Data
public class LogHubProducerConfig {
	private String store;
	private String topic = "";
	private SourceType source = SourceType.HOST_IP;
	private String converter = DefaultLogItemConverter.class.getName();
}
