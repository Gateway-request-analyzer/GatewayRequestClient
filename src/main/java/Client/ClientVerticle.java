package Client;

import Proxy.AuthClient;
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
  private AuthClient auth;

  @Override
  public void start() throws Exception {

      Random rand = new Random();
      int randPort = rand.nextInt(3) + 3000;
      //use randPort instead of static "3000" port in case of 3 clients
      //change bound of nextInt in case of more clients
      this.vertx.createHttpClient().webSocket(3000, "localhost", "/")
        .onComplete(socket -> {
            HttpServer server = this.vertx.createHttpServer();
            GraClient client = new GraClient(vertx, auth);
        });

  }
}
