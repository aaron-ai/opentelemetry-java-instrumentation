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
import org.apache.rocketmq.client.java.message.PublishingMessageImpl;

public class ParentContextExtractor {
  private ParentContextExtractor() {}

  public static Context fromMessage(PublishingMessageImpl message) {
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

    @SuppressWarnings("NullableProblems")
    @Override
    public String get(Map<String, String> carrier, String key) {
      assert carrier != null;
      return carrier.get(key);
    }
  }
}
