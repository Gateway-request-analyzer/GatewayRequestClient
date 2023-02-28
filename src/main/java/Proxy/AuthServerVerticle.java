package Proxy;

import io.vertx.core.AbstractVerticle;

public class AuthServerVerticle extends AbstractVerticle {

  @Override
  public void start() {
    System.out.println("In AuthServerVerticle right now");
      AuthServer authServer = new AuthServer(vertx.createHttpServer(), vertx);
      System.out.println("authServer created");
  }
}
