/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmqclientjava.v5_0;

import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingAttributesGetter;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import javax.annotation.Nullable;
import org.apache.rocketmq.client.java.impl.producer.SendReceiptImpl;
import org.apache.rocketmq.client.java.message.PublishingMessageImpl;

enum RocketMqProducerAttributeGetter
    implements MessagingAttributesGetter<PublishingMessageImpl, SendReceiptImpl> {
  INSTANCE;

  @Nullable
  @Override
  public String system(PublishingMessageImpl message) {
    return "rocketmq";
  }

  @Nullable
  @Override
  public String destinationKind(PublishingMessageImpl message) {
    return SemanticAttributes.MessagingDestinationKindValues.TOPIC;
  }

  @Nullable
  @Override
  public String destination(PublishingMessageImpl message) {
    return message.getTopic();
  }

  @Override
  public boolean temporaryDestination(PublishingMessageImpl message) {
    return false;
  }

  @Nullable
  @Override
  public String protocol(PublishingMessageImpl message) {
    return null;
  }

  @Nullable
  @Override
  public String protocolVersion(PublishingMessageImpl message) {
    return null;
  }

  @Nullable
  @Override
  public String url(PublishingMessageImpl message) {
    return null;
  }

  @Nullable
  @Override
  public String conversationId(PublishingMessageImpl message) {
    return null;
  }

  @Nullable
  @Override
  public Long messagePayloadSize(PublishingMessageImpl message) {
    return (long) message.getBody().remaining();
  }

  @Nullable
  @Override
  public Long messagePayloadCompressedSize(PublishingMessageImpl message) {
    return null;
  }

  @Nullable
  @Override
  public String messageId(PublishingMessageImpl message, @Nullable SendReceiptImpl sendReceipt) {
    return message.getMessageId().toString();
  }
}
