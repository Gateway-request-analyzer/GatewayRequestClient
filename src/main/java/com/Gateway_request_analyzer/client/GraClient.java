package com.Gateway_request_analyzer.client;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.net.Socket;
import java.util.Random;


public class GraClient {

  public Vertx vertx;
  public Future<WebSocket> socket;
  int responseCounter = 0;

  public GraClient(Vertx vertx){
    this.vertx = vertx;
    connectToServer();
  }

  public void sendEvent(String ip, String userId, String session, String URI){

    JsonObject jo = new JsonObject();


    jo.put("ip", ip).put("userId", userId).put("session", session).put("URI", URI);

    Buffer json = Json.encodeToBuffer(jo);

    socket.onSuccess(handler ->{
        handler.writeBinaryMessage(json);
        handler.binaryMessageHandler(res ->{
            System.out.println("Response from server: " + res);

        });
    });





  }

  private void connectToServer(){
    int rand = randSetup();
    this.socket = this.vertx.createHttpClient().webSocket(rand + 3000, "localhost", "/");
  }
  private int randSetup(){
    Random rand = new Random();
    return rand.nextInt(3);
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
