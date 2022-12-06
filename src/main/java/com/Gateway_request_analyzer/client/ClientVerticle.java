package com.Gateway_request_analyzer.client;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.spi.json.JsonCodec;


public class ClientVerticle extends AbstractVerticle {

  private HttpClient httpClient;
  private WebSocket socket;

  @Override
  public void start() throws Exception {
    startClient(vertx);
  }

      private void startClient(Vertx vertx){
        GraClient client = new GraClient(vertx);
        for(int i = 0; i < 1000; i++) { // Server logs OK
          client.sendEvent("1.2.3.5", "user1", "session1", "/");
        }

        /*
        client.sendEvent("1.2.3.4", "user1", "session1", "/"); // Server logs OK
        client.sendEvent("2.2.3.4", "user1", "session1", "/page1"); // Server logs OK

        // Server logs that the request is rate limited, but we don't yet have a way to signal this back to the client. We'll deal with this later
        client.sendEvent("3.2.3.4", "user1", "session1", "/page2");

        // We wait some to clear the rate limit
        Thread.sleep(2000);

        vertx.wait(2000);

        // The rate limit should be cleared and server should log OK
        client.sendEvent("1.2.3.4", "user1", "session1", "/page3");
        */


    }
  }

  /*
  * TODO:
  *  1: Sätt upp dummy object för att skicka till server
  *  2: Få detta att packa binärt för överföring, översätt på båda sidor
  *
  * */
