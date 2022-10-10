plugins {
  id("otel.library-instrumentation")
}

dependencies {
  library("org.apache.rocketmq:rocketmq-client-java:5.0.3-SNAPSHOT")
}
