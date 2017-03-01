package com.github.charleslzq.loghub.binder.extract;

import com.github.charleslzq.loghub.converter.LogConverter;
import org.springframework.cloud.sleuth.stream.Spans;

/**
 * Created by Charles on 3/1/2017.
 */
public interface SpansExtractor extends LogConverter<Spans> {
}
