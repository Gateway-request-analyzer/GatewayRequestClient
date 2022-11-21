package com.Gateway_request_analyzer.client;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;


public class GraClient {

  public Vertx vertx;
  public HttpClient client;

  public void Main(String[] args) throws InterruptedException{


  }
  public GraClient(Vertx vertx){
    this.vertx = vertx;
    this.client = vertx.createHttpClient();
  }

  public void sendEvent(String ip, String userId, String session, String URI){

    serverResponse();

    JsonObject jo = new JsonObject();


    jo.put("ip", ip).put("userId", userId).put("session", session).put("URI", URI);

    Buffer json = Json.encodeToBuffer(jo);


    client.webSocket(3000, "localhost", "/", websocket -> {
      if(websocket.succeeded()){

        WebSocket socket = websocket.result();

        socket.writeBinaryMessage(json);
        socket.handler(data -> System.out.println(data.toString()));
        socket.end();


      } else{
        System.out.println("Something went wrong" + websocket.cause().getCause());
      }
    });

  }

  private void serverResponse(){

    vertx.createHttpServer().webSocketHandler(handler -> {
      System.out.println("Received response from server");

      handler.textMessageHandler(msg -> {

        System.out.println("Message received: " + msg);
      });

      handler.end();

    }).listen(3500);

  }
}
