/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.rocketmqclientjava.v5_0;

import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.Collections;
import java.util.List;

public class RocketMqInstrumentationModule extends InstrumentationModule {
  public RocketMqInstrumentationModule() {
    super("rocketmq-client", "rocketmq-client-java-5.0");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return Collections.singletonList(new RocketMqProducerInstrumentation());
  }
}
