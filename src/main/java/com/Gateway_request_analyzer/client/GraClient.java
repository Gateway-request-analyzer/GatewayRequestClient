package com.Gateway_request_analyzer.client;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.FutureTask;


public class GraClient {

  public Vertx vertx;
  public WebSocket socket;
  private HttpServer server;

  //list of blocked users/ip/sessions
  private HashSet<String> blockedList = new HashSet<>();
  int responseCounter = 0;

  public GraClient(Vertx vertx, WebSocket socket, HttpServer server) {
    this.vertx = vertx;
    this.socket = socket;
    this.server = server;
    setUpHandlers();
  }

  public void sendEvent(String ip, String userId, String session) {

    //check if any given parameter is already blocked
    if(!blockedList.contains(ip) && !blockedList.contains(userId) && !blockedList.contains(session)) {

      JsonObject jo = new JsonObject();
      jo.put("ip", ip).put("userId", userId).put("session", session);

      Buffer json = Json.encodeToBuffer(jo);
      socket.writeBinaryMessage(json);
    }else{
      System.out.println("Currently blocked");
    }
  }

  /**
   * Sets up the required handlers used by the client
   *
   * server.requestHandler handles incoming HTTP-requests and
   * forwards headers to GRAserver
   *
   * socket.BinaryMessageHandler handles replies/messages from GRAserver which
   * are currently exclusively from pub/sub. The response is always a
   * JsonObject Buffer
   *
   * vertx.setPeriodic unblocks the currently blocked users/ip/sessions by
   * emptying the hashSet each minute
   */
  private void setUpHandlers(){

    this.server.requestHandler(handler ->{
      MultiMap headers = handler.headers();
      sendEvent(headers.get("ip_address"), headers.get("userId"), headers.get("session"));
      handler.response().end();

    }).listen(7890);

    this.socket.binaryMessageHandler(res -> {
      System.out.println("Response from server: " + res);
      JsonObject json = (JsonObject) Json.decodeValue(res);

      //check if the message is for a single user/ip/sessions, type is "single"
      if(Objects.equals(json.getString("type"), "single")) {
        blockedList.add(json.getString("identifier"));
        System.out.println("This identifier was blocked: " + json.getString("identifier"));

      //check if the message is to update the blockedList, type is "saveState"
      }else if(Objects.equals(json.getString("type"), "saveState")){

        //remove "type":"saveState" and iterate over entire list to add each value to blockList
        json.remove("type");
        for(Map.Entry<String, Object> item : json){
          String s = (String) item.getValue();
          System.out.println("Value from saveState: " + s);
          blockedList.add(s);
        }
      }

    });

    this.vertx.setPeriodic(60000, handler ->{
      this.blockedList = new HashSet<>();
    });
  }
}
