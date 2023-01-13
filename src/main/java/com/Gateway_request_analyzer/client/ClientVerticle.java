package com.Gateway_request_analyzer.client;

import io.vertx.core.*;
import io.vertx.core.http.*;
import java.util.Random;

/**
 * Class for opening connection to GRAServer via websocket.
 * A GraClient instance is created if the connection is successfull.
 */

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
