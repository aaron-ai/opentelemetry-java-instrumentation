/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmqclientjava.v5_0;

import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import java.util.Collections;
import java.util.Optional;
import org.apache.rocketmq.client.apis.message.Message;

public class ParentContextExtractor {
  private ParentContextExtractor() {}

  public static Context fromMessage(Message message) {
    Optional<String> parentTraceContext = message.getParentTraceContext();
    if (!parentTraceContext.isPresent()) {
      return Context.root();
    }
    return W3CTraceContextPropagator.getInstance()
        .extract(Context.root(), Collections.singletonMap("a", "b"), null);
  }
}
