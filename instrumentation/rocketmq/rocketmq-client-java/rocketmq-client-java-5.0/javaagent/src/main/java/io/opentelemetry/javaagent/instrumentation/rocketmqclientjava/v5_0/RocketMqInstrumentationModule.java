/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.rocketmqclientjava.v5_0;

import static java.util.Arrays.asList;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class RocketMqInstrumentationModule extends InstrumentationModule {
  public RocketMqInstrumentationModule() {
    super("rocketmq-client-java", "rocketmq-client-java-5.0");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    System.out.println("xxx");
    return asList(
        new RocketMqProducerInstrumentation(),
        new RocketMqPushConsumerInstrumentation(),
        new RocketMqSimpleConsumerInstrumentation());
  }
}
