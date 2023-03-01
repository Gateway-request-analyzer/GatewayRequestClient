package Proxy;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.*;
import io.vertx.ext.web.client.WebClient;

import java.sql.SQLOutput;
import java.util.function.Consumer;

public class AuthClient {
  Vertx vertx;
  private User user;
  public AuthClient(Vertx vertx){
    this.vertx = vertx;
  }

  // TODO: Get specific grant from config
  public JsonObject getGrant(){
    return new JsonObject().put("grant", "<grant>");
  }

  // TODO: Use grant to get token from auth server to use when calling rateLimiter
  // Consumer<String> token, Consumer<String> onTestFailure för consumer pattern
  public Future<User> generateToken(){
    //WebClient client = WebClient.create(vertx);
    OAuth2Options credentials = new OAuth2Options()
      .setFlow(OAuth2FlowType.CLIENT)
      .setClientId("client1")
      .setClientSecret("secret123")
      .setSite("http://localhost:8888");


// Initialize the OAuth2 Library
    OAuth2Auth oauth2 = OAuth2Auth.create(vertx, credentials);

    JsonObject tokenConfig = new JsonObject();

    return oauth2.authenticate(tokenConfig)
      .onSuccess(user -> {
        this.user = user;


        String tokenStr = user.get("access_token");
        String expTime = user.get("expires_in");

        // for consumer pattern
        //token.accept(tokenStr);
/*
        System.out.println("token from server: ");
        System.out.println(tokenStr);
        System.out.println("exptime: ");
        System.out.println(expTime);
*/
        //TODO: get this token to proxy/client
      })
      .onFailure(err -> {
        System.err.println("Access Token Error: " + err.getMessage());
        // For consumer pattern
        //onTestFailure.accept("Access Token Error " + err.getMessage());
      });
  }

  public User getUser() {
    return user;
  }

  public String getToken(){
    return this.user.get("access_token");
  }
}
