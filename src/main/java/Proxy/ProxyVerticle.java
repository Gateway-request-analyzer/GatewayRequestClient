package Proxy;

import Client.GraClient;
import io.vertx.core.*;
import io.vertx.core.http.*;

import java.util.List;

public class ProxyVerticle extends AbstractVerticle {

  private HttpClient httpClient;
  private WebSocket socket;

  @Override
  public void start() {

  this.clientSetup();


  }

  private void clientSetup(){

    this.vertx.createHttpClient().webSocket(3000, "localhost", "/")
      .onComplete(socket -> {
        HttpServer server = this.vertx.createHttpServer();

        GraClient client = new GraClient(vertx, socket.result());
        GraProxy proxy = new GraProxy(vertx, server, client);
      });
  }


}

/**
 * TODO:
 * In verticle:
 * Open server connection
 *
 * Send connection to Proxy (Start proxy)
 * Start Client
 * Read config
 *
 * In Proxy:
 * HTTP-server to accept requests & forward to endpoint
 * Hashmaps for blocked addresses
 *
 * Start client
 *
* */
