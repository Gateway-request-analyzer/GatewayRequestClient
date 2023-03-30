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
     proxySetup();

    }).onFailure(error -> {
      System.out.println("Error establishing pub/sub and/or redis connection: " + error.getMessage());
    });
  }

  private void proxySetup(){

    /*
    // TODO: Make client with consumer pattern, send in tempClient instead of socket and return a status when done to make sure execution is correct.
    this.vertx.createHttpClient().webSocket(options).onComplete(socket -> {
      HttpServer server = this.vertx.createHttpServer();
      GraClient client = new GraClient(vertx, socket.result(), authClient);
      GraProxy proxy = new GraProxy(vertx, server, client, authClient);

    });
    */

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

