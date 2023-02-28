package Proxy;

import Client.GraClient;
import io.vertx.core.*;
import io.vertx.core.http.*;


public class ProxyVerticle extends AbstractVerticle {

  @Override
  public void start() {


  this.clientSetup();
  vertx.deployVerticle(new AuthServerVerticle());
  System.out.println("deployed authserver");


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

