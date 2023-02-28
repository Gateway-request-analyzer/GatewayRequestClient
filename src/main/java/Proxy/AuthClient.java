package Proxy;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.*;
import io.vertx.ext.web.client.WebClient;

public class AuthClient {
  Vertx vertx;
  private User user;
  public AuthClient(Vertx vertx){
    this.vertx = vertx;
    getToken();

  }

  // TODO: Get specific grant from config
  public JsonObject getGrant(){
    return new JsonObject().put("grant", "<grant>");
  }

  // TODO: Use grant to get token from auth server to use when calling rateLimiter
  public void getToken(){
    //WebClient client = WebClient.create(vertx);

    OAuth2Options credentials = new OAuth2Options()
      .setFlow(OAuth2FlowType.CLIENT)
      .setClientId("<client-id>")
      .setClientSecret("<client-secret>")
      .setSite("http://localhost:8888");


// Initialize the OAuth2 Library
    OAuth2Auth oauth2 = OAuth2Auth.create(vertx, credentials);

    JsonObject tokenConfig = new JsonObject();

    oauth2.authenticate(tokenConfig)
      .onSuccess(user -> {
        this.user = user;

        String token = user.get("access_token");
        String expTime = user.get("expires_in");
        //TODO: fetch
      })
      .onFailure(err -> {
        System.err.println("Access Token Error: " + err.getMessage());
      });
  }
}
