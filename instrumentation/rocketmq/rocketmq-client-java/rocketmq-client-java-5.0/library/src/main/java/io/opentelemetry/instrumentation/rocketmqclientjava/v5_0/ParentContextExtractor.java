/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmqclientjava.v5_0;

import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.rocketmq.client.apis.message.Message;

public class ParentContextExtractor {
  private ParentContextExtractor() {}

  public static Context fromMessage(Message message) {
    Optional<String> parentTraceContext = message.getParentTraceContext();
    if (!parentTraceContext.isPresent()) {
      return Context.root();
    }
    return W3CTraceContextPropagator.getInstance()
        .extract(
            Context.root(),
            Collections.singletonMap("traceparent", parentTraceContext.get()),
            MapGetter.INSTANCE);
  }

  private enum MapGetter implements TextMapGetter<Map<String, String>> {
    INSTANCE;

    @Override
    public Iterable<String> keys(Map<String, String> carrier) {
      return carrier.keySet();
    }

    @Nullable
    @Override
    public String get(@Nullable Map<String, String> carrier, String key) {
      return carrier.get(key);
    }
  }
}
