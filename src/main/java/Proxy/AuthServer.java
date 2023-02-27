package Proxy;

import io.vertx.core.http.HttpServerRequest;

public class AuthServer {

  HttpServerRequest request;

  public AuthServer(){
  }

  // TODO: Generate token and return it to requester
  // Använd client credential, reject everything else
  private void generateToken(HttpServerRequest request){

    request.response().end("<token>");

  }

}
