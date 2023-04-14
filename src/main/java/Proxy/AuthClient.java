package Proxy;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import com.auth0.jwt.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.*;
import io.vertx.ext.web.client.WebClient;

import java.sql.SQLOutput;
import java.util.Date;
import java.util.function.Consumer;

public class AuthClient {

  private Vertx vertx;
  private User user;

  public AuthClient(Vertx vertx){
    this.vertx = vertx;
  }

  // TODO: Get specific grant from config
  private JsonObject getGrant(){
    return new JsonObject().put("grant", "<grant>");
  }

  // TODO: Use grant to get token from auth server to use when calling rateLimiter
  // for consumer pattern Consumer<String> token, Consumer<String> onTestFailure
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

        System.out.println("This is the user: " + this.user);

        String tokenStr = user.get("access_token");
        String expTime = user.get("expires_in");
        this.tokenUpdater();
        // for consumer pattern
        //token.accept(tokenStr);
      })
      .onFailure(err -> {
        System.err.println("Access Token Error: " + err.getMessage());
        this.tokenUpdater();
        // For consumer pattern
        //onTestFailure.accept("Access Token Error " + err.getMessage());
      });

  }

  public String refreshToken(){
    this.generateToken().onComplete(handler -> {
      this.user = handler.result();
    }).onFailure(error -> {
      System.out.println("Error refreshing token " + error.getMessage());
    });
    return this.getToken();
  }


  private void tokenUpdater(){
    this.vertx.setTimer(280*1000, handler -> {
      System.out.println("Updating token");
      this.generateToken();
    });
  }

  public String getToken(){
    return this.user.get("access_token");
  }
}
