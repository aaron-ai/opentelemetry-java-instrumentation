/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.rocketmqclientjava.v5_0;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import java.util.List;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.rocketmq.client.java.impl.producer.ProducerImpl;
import org.apache.rocketmq.client.java.impl.producer.SendReceiptImpl;
import org.apache.rocketmq.client.java.message.PublishingMessageImpl;
import org.apache.rocketmq.shaded.com.google.common.util.concurrent.FutureCallback;
import org.apache.rocketmq.shaded.com.google.common.util.concurrent.Futures;
import org.apache.rocketmq.shaded.com.google.common.util.concurrent.MoreExecutors;
import org.apache.rocketmq.shaded.com.google.common.util.concurrent.SettableFuture;

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
    public static void onEnter(
        @Advice.This ProducerImpl producer,
        @Advice.Argument(0) SettableFuture<List<SendReceiptImpl>> future,
        @Advice.Argument(4) List<PublishingMessageImpl> messages) {
      Context parentContext = Context.current();
      Instrumenter<PublishingMessageImpl, SendReceiptImpl> producerInstrumenter =
          RocketMqSingletons.producerInstrumenter();
      int count = messages.size();
      for (int i = 0; i < count; i++) {
        PublishingMessageImpl message = messages.get(i);
        if (!producerInstrumenter.shouldStart(parentContext, message)) {
          return;
        }
        Context context = producerInstrumenter.start(parentContext, message);
        int j = i;
        Futures.addCallback(
            future,
            new FutureCallback<List<SendReceiptImpl>>() {
              @Override
              public void onSuccess(List<SendReceiptImpl> sendReceipts) {
                SendReceiptImpl sendReceipt = sendReceipts.get(j);
                producerInstrumenter.end(context, message, sendReceipt, null);
              }

              @SuppressWarnings("NullableProblems")
              @Override
              public void onFailure(Throwable throwable) {
                producerInstrumenter.end(context, message, null, throwable);
              }
            },
            MoreExecutors.directExecutor());
      }
    }
  }
}
