package com.Gateway_request_analyzer.client;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.net.Socket;
import java.util.Random;
import java.util.concurrent.FutureTask;


public class GraClient {

  public Vertx vertx;
  public WebSocket socket;
  int responseCounter = 0;

  public GraClient(Vertx vertx, WebSocket socket) {
    this.vertx = vertx;
    this.socket = socket;
    multiSend();
  }

  public void sendEvent(String ip, String userId, String session, String URI) {

    JsonObject jo = new JsonObject();

    jo.put("ip", ip).put("userId", userId).put("session", session).put("URI", URI);

    Buffer json = Json.encodeToBuffer(jo);

      socket.writeBinaryMessage(json);
      socket.binaryMessageHandler(res -> {
        System.out.println("Response from server: " + res);

      });
  }

  //amounts of requests sent
  private void multiSend(){
    for(int i = 0; i < 20; i++) { // Server logs OK
      this.sendEvent("1.2.3.5", "user1", "session1", "/");
    }
  }

  private void connectToServer() {


    clientToServerSetup().onComplete(handler -> {
      this.multiSend();
    }).onFailure(error -> {
      System.out.println("Connection failed: " + error.getMessage());
    });

  }


  private Future<WebSocket> clientToServerSetup() {
    Random rand = new Random();
    return this.vertx.createHttpClient().webSocket(rand.nextInt(2) + 3000, "localhost", "/");
  }


}
/*
client.webSocket(3001, "localhost", "/", websocket -> {
      if(websocket.succeeded()){

        this.socket = websocket.result();

        socket.writeBinaryMessage(json);
        //socket.handler(data -> System.out.println(data.toString()));
        socket.binaryMessageHandler(msg ->{
          System.out.println("Response: " + msg);
        });


      } else{
        System.out.println("Something went wrong" + websocket.cause().getCause());
      }
    });
 */
