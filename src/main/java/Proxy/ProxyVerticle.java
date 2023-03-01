package Proxy;

import Client.GraClient;
import io.vertx.core.*;
import io.vertx.core.http.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.RedisOptions;

import java.util.List;


public class ProxyVerticle extends AbstractVerticle {

  AuthClient authClient;

  @Override
  public void start() {

  vertx.deployVerticle(new AuthServerVerticle());

  this.authClient = new AuthClient(vertx);
    CompositeFuture.all(List.of(
      generateFirstToken(authClient))
    ).onComplete(handler -> {
     clientSetup();

    }).onFailure(error -> {
      System.out.println("Error establishing pub/sub and/or redis connection: " + error.getMessage());
    });



  }

  private void clientSetup(){

    WebSocketConnectOptions options = new WebSocketConnectOptions()
      .setHost("localhost")
      .setPort(3000)
      .setURI("/")
      .addHeader("Authorization", authClient.getToken());

    this.vertx.createHttpClient().webSocket(options).onComplete(socket -> {
      HttpServer server = this.vertx.createHttpServer();
      GraClient client = new GraClient(vertx, socket.result());
      GraProxy proxy = new GraProxy(vertx, server, client, authClient);
    });
/*
    this.vertx.createHttpClient().webSocket(3000, "localhost", "/")
      .onComplete(socket -> {

      });
      */

  }

  public Future<User> generateFirstToken(AuthClient authClient){
    return authClient.generateToken();
  }






}

