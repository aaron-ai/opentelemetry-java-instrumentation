plugins {
  id("otel.javaagent-instrumentation")
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  library("org.apache.rocketmq:rocketmq-client-java:5.0.3-SNAPSHOT")

  implementation(project(":instrumentation:rocketmq:rocketmq-client-java:rocketmq-client-java-5.0:library"))

  testImplementation(project(":instrumentation:rocketmq:rocketmq-client-java:rocketmq-client-java-5.0:testing"))

  testLibrary("org.apache.rocketmq:rocketmq-test:4.8.0")
}
