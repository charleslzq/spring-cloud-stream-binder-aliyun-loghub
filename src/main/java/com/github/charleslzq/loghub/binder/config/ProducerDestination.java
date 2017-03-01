package com.github.charleslzq.loghub.binder.config;

import com.github.charleslzq.loghub.config.LogHubProjectConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Charles on 3/1/2017.
 */
@Data
@AllArgsConstructor
public class ProducerDestination {
	private LogHubProjectConfig project;
}
