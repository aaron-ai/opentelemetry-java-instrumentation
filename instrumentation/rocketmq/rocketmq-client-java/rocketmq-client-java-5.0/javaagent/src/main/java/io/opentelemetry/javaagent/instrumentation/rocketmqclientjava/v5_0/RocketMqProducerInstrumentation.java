/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.rocketmqclientjava.v5_0;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import java.util.List;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.rocketmq.client.java.impl.producer.ProducerImpl;

public class RocketMqProducerInstrumentation implements TypeInstrumentation {
  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("org.apache.rocketmq.client.java.impl.producer.ProducerImpl");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(named("send0"))
            .and(takesArguments(6))
            .and(
                takesArgument(
                    0,
                    named(
                        "org.apache.rocketmq.shaded.com.google.common.util.concurrent.SettableFuture")))
            .and(takesArgument(1, String.class))
            .and(takesArgument(2, named("org.apache.rocketmq.client.java.message.MessageType")))
            .and(takesArgument(3, List.class))
            .and(takesArgument(4, List.class))
            .and(takesArgument(5, int.class)),
        RocketMqProducerInstrumentation.class.getName() + "$StartAdvice");
  }

  @SuppressWarnings("unused")
  public static class StartAdvice {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(@Advice.This ProducerImpl producer) {
      System.out.println(producer);
    }
  }
}
