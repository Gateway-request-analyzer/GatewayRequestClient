package Proxy;

import Client.GraClient;
import Testing.TestingEndpoint;
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
    vertx.deployVerticle(new TestingEndpoint());

  this.authClient = new AuthClient(vertx);
    CompositeFuture.all(List.of(
      generateFirstToken(authClient))
    ).onComplete(handler -> {
      System.out.println("Successfully received token from auth server");
     proxySetup();

    }).onFailure(error -> {
      System.out.println("Error establishing pub/sub and/or redis connection: " + error.getMessage());
    });
  }

  private void proxySetup(){

    GraClient client = new GraClient(vertx, authClient);

    client.webSocketSetup(success -> {
      System.out.println(success);
      HttpServer server = this.vertx.createHttpServer();
      GraProxy proxy = new GraProxy(vertx, server, client, authClient);
    }, error -> {
      this.vertx.setTimer(10*1000, handler -> {
        System.out.println(error);
        this.proxySetup();
      });
    });




  }
  public Future<User> generateFirstToken(AuthClient authClient){
    return authClient.generateToken();
  }
}

