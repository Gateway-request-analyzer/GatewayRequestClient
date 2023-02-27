package Proxy;

import io.vertx.core.AbstractVerticle;

public class AuthServerVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.createHttpServer().requestHandler(handler -> {
      AuthServer authServer = new AuthServer();

    }).listen(8888);
  }
}
