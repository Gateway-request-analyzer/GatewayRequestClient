package com.Gateway_request_analyzer.client;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.net.Socket;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.FutureTask;


public class GraClient {

  public Vertx vertx;
  public WebSocket socket;
  private HttpServer server;
  private HashSet<String> blockedList = new HashSet<>();
  int responseCounter = 0;

  public GraClient(Vertx vertx, WebSocket socket, HttpServer server) {
    this.vertx = vertx;
    this.socket = socket;
    this.server = server;
    setUpHandlers();
    //multiSend();
  }

  public void sendEvent(String ip, String userId, String session) {

    if(!blockedList.contains(ip) || !blockedList.contains(userId) || !blockedList.contains(session)) {

      JsonObject jo = new JsonObject();
      jo.put("ip", ip).put("userId", userId).put("session", session);

      Buffer json = Json.encodeToBuffer(jo);
      socket.writeBinaryMessage(json);
    }else{
      System.out.println("Currently blocked");
    }
  }

  //amounts of requests sent
  private void multiSend(){

    for(int i = 0; i < 50; i++) { // Server logs OK
      this.sendEvent("1.2.3.3", "user1", "session1");
      try {
        Thread.sleep(50);
      }catch(Exception e){
        System.out.println("fel");
      }

    }

/*
    this.sendEvent("1.2.3.5", "user1", "session1", "/");



    this.sendEvent("1.2.3.5", "user1", "session1", "/");

    this.sendEvent("1.2.3.5", "user1", "session1", "/");

    this.sendEvent("1.2.3.5", "user1", "session1", "/");
*/


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
    int randPort = rand.nextInt(2) + 3000;
    return this.vertx.createHttpClient().webSocket(3000, "localhost", "/");
  }

  private void setUpHandlers(){

    this.server.requestHandler(handler ->{
      MultiMap headers = handler.headers();
      sendEvent(headers.get("ip_address"), headers.get("userId"), headers.get("session"));
      handler.response().end();

    }).listen(7890);

    this.socket.binaryMessageHandler(res -> {
      System.out.println("Response from server: " + res);
      JsonObject json = (JsonObject) Json.decodeValue(res);

      blockedList.add(json.getString("identifier"));
      System.out.println("This identifier was blocked: " + json.getString("identifier"));

    });

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
