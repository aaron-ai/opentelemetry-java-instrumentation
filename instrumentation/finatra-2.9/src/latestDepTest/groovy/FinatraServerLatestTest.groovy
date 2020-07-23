/*
 * Copyright The OpenTelemetry Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static io.opentelemetry.auto.test.base.HttpServerTest.ServerEndpoint.SUCCESS
import static io.opentelemetry.trace.Span.Kind.INTERNAL

import com.twitter.app.lifecycle.Event
import com.twitter.app.lifecycle.Observer
import com.twitter.finatra.http.HttpServer
import com.twitter.util.Await
import com.twitter.util.Closable
import com.twitter.util.Duration
import com.twitter.util.Promise
import io.opentelemetry.auto.test.asserts.TraceAssert
import io.opentelemetry.auto.test.base.HttpServerTest
import io.opentelemetry.sdk.trace.data.SpanData

class FinatraServerLatestTest extends HttpServerTest<HttpServer> {
  private static final Duration TIMEOUT = Duration.fromSeconds(5)
  private static final Duration STARTUP_TIMEOUT = Duration.fromSeconds(20)

  static closeAndWait(Closable closable) {
    if (closable != null) {
      Await.ready(closable.close(), TIMEOUT)
    }
  }

  @Override
  HttpServer startServer(int port) {
    HttpServer testServer = new FinatraServer()

    // Starting the server is blocking so start it in a separate thread
    Thread startupThread = new Thread({
      testServer.main("-admin.port=:0", "-http.port=:" + port)
    })
    startupThread.setDaemon(true)
    startupThread.start()

    Promise<Boolean> startupPromise = new Promise<>()

    testServer.withObserver(new Observer() {
      @Override
      void onSuccess(Event event) {
        if (event == testServer.startupCompletionEvent()) {
          startupPromise.setValue(true)
        }
      }

      void onEntry(Event event) {

      }

      @Override
      void onFailure(Event stage, Throwable throwable) {
        if (stage != Event.Close$.MODULE$) {
          startupPromise.setException(throwable)
        }
      }
    })

    Await.result(startupPromise, STARTUP_TIMEOUT)

    return testServer
  }

  @Override
  boolean hasHandlerSpan() {
    return true
  }

  @Override
  boolean testNotFound() {
    // Resource name is set to "GET /notFound"
    false
  }

  @Override
  void stopServer(HttpServer httpServer) {
    Await.ready(httpServer.close(), TIMEOUT)
  }

  @Override
  void handlerSpan(TraceAssert trace, int index, Object parent, String method = "GET", ServerEndpoint endpoint = SUCCESS) {
    trace.span(index) {
      operationName "FinatraController"
      spanKind INTERNAL
      childOf(parent as SpanData)
      // Finatra doesn't propagate the stack trace or exception to the instrumentation
      // so the normal errorAttributes() method can't be used
      errored false
      attributes {
      }
    }
  }
}
