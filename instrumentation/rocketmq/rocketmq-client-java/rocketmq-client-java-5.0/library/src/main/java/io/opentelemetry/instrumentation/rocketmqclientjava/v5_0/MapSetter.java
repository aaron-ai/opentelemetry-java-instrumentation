/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmqclientjava.v5_0;

import io.opentelemetry.context.propagation.TextMapSetter;
import javax.annotation.Nullable;
import org.apache.rocketmq.client.java.message.PublishingMessageImpl;

enum MapSetter implements TextMapSetter<PublishingMessageImpl> {
  INSTANCE;

  @Override
  public void set(@Nullable PublishingMessageImpl carrier, String key, String value) {
    if (null == carrier || !"traceparent".equals(key)) {
      return;
    }
    carrier.setTraceContext(value);
  }
}
