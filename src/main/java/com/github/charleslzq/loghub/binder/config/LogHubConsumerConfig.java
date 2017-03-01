package com.github.charleslzq.loghub.binder.config;

import com.aliyun.openservices.loghub.client.config.LogHubCursorPosition;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Charles on 3/1/2017.
 */
@Data
public class LogHubConsumerConfig {
	private String store;
	private String group;
	private List<String> topics = new ArrayList<>();
	private LogHubCursorPosition cursorPosition = LogHubCursorPosition.END_CURSOR;
	private int startTime = 0;
	private long fetchIntervalMillis = 200;
	private long heartBeatIntervalMillis = 1000;
	private boolean keepOrder = true;
}
