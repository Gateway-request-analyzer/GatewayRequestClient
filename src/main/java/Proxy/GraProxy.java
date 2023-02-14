package Proxy;

import Client.GraClient;
import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;


public class GraProxy {
  private Vertx vertx;
  private HttpServer server;
  private GraClient internalClient;
  private WebClient webClient;
  private int statusCode;

  public GraProxy(Vertx vertx, HttpServer server, GraClient internalClient){
    this.vertx = vertx;
    this.server = server;
    this.internalClient = internalClient;
    this.webClient = WebClient.create(vertx);
    this.setUpHandlers();
  }

  private void setUpHandlers(){
    this.server.requestHandler(handler ->{
      internalClient.updateBlockedList();

      MultiMap headers = handler.headers();
      String ip = headers.get("ip_address");
      String session = headers.get("session");
      String userId = headers.get("userId");
      String URL = headers.get("Request URL");
      String reqMethod = headers.get("Request Method");
      if(internalClient.checkBlockedList(ip, session, userId)){
        internalClient.sendEvent(headers.get("ip_address"), headers.get("userId"), headers.get("session"));
        // TODO: fetch public API and return data as response
        this.proxyEndpointFetch(URL, reqMethod, handler);
        statusCode = 200;
      } else {
        System.out.println("This user is currently blocked: " + ip);
        statusCode = 429;
      }
      handler.response().setStatusCode(statusCode).end();

    }).listen(7890);
    System.out.println("handlers set up");
  }

  private void proxyEndpointFetch(String URL, String reqMethod, HttpServerRequest request){
    this.webClient
      .getAbs(URL)
      .send()
      .onSuccess(handler -> {
        System.out.println("Message body received: " + handler.body().toString());

      }).onFailure(err -> {
        System.out.println("Error checking cat breeds: " + err.getMessage());
      });

  }
}
