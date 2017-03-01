package com.github.charleslzq.loghub.binder.config;

import lombok.Data;

/**
 * Created by Charles on 3/1/2017.
 */
@Data
public class LogHubProducerParameter {
	private int packageTimeOutMs = 3000;
	private int logPerPackage = 4096;
	private int bytesPerPackage = 5242880;
	private int maxMemSize = 1048576000;
	private int maxIOThread = 50;
	private int updateIntervalMs = 600000;
	private int retry = 3;
}
