plugins {
  id("otel.library-instrumentation")
}

dependencies {
  library("org.apache.rocketmq:rocketmq-client-java:5.0.2")
  testImplementation(project(":instrumentation:rocketmq:rocketmq-client-java:rocketmq-client-java-5.0:testing"))
}
