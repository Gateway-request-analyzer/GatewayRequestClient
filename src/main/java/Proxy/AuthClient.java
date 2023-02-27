package Proxy;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.*;

public class AuthClient {
  Vertx vertx;
  public AuthClient(Vertx vertx){
    this.vertx = vertx;

  }

  // TODO: Get specific grant from config
  public JsonObject getGrant(){
    return new JsonObject().put("grant", "<grant>");
  }

  // TODO: Use grant to get token from auth server to use when calling rateLimiter
  public void getToken(){
    JsonObject grant = getGrant();


  }
}
