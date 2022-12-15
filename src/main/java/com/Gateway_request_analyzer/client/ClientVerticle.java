package com.Gateway_request_analyzer.client;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.spi.json.JsonCodec;

import java.util.Random;


public class ClientVerticle extends AbstractVerticle {

  private HttpClient httpClient;
  private WebSocket socket;

  @Override
  public void start() throws Exception {



      Random rand = new Random();
      this.vertx.createHttpClient().webSocket(rand.nextInt(3) + 3000, "localhost", "/")
        .onComplete(socket -> {
          GraClient client = new GraClient(vertx, socket.result());
        });


  }
}


  /*
  * TODO:
  *  1: Sätt upp dummy object för att skicka till server
  *  2: Få detta att packa binärt för överföring, översätt på båda sidor
  *
  * */
