package com.github.charleslzq.loghub.binder.extract;

import com.aliyun.openservices.log.common.Logs;
import com.google.gson.Gson;
import org.springframework.cloud.sleuth.stream.Spans;

/**
 * Created by Charles on 3/1/2017.
 */
public class SimpleSpansExtractor implements SpansExtractor {
	private Gson gson = new Gson();

	@Override
	public Spans convert(Logs.Log log) {
		return gson.fromJson(getMessage(log), Spans.class);
	}

	private String getMessage(Logs.Log log) {
		return log.getContentsList().stream()
				.filter(content -> "message".equals(content.getKey()))
				.findAny()
				.map(Logs.Log.Content::getValue)
				.orElseThrow(
						() -> new IllegalArgumentException("Can't find message in logs")
				);
	}
}
