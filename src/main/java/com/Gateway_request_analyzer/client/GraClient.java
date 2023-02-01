package com.Gateway_request_analyzer.client;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.FutureTask;


public class GraClient {

  public Vertx vertx;
  public WebSocket socket;
  private HttpServer server;

  //list of blocked users/ip/sessions

  private HashMap<String, Long> blockedIP = new HashMap<>();
  private HashMap<String, Long> blockedSession = new HashMap<>();
  private HashMap<String, Long> blockedUserId = new HashMap<>();
  int statuscode = 429;

  public GraClient(Vertx vertx, WebSocket socket, HttpServer server) {
    this.vertx = vertx;
    this.socket = socket;
    this.server = server;
    setUpHandlers();
  }

  public void sendEvent(String ip, String userId, String session) {

      JsonObject jo = new JsonObject();
      jo.put("ip", ip).put("userId", userId).put("session", session);

      Buffer json = Json.encodeToBuffer(jo);
      socket.writeBinaryMessage(json);
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
      String ip = headers.get("ip_address");
      String session = headers.get("session");
      String userId = headers.get("userId");
      if(!blockedIP.containsKey(ip) && !blockedUserId.containsKey(userId) && !blockedSession.containsKey(session)){
        sendEvent(headers.get("ip_address"), headers.get("userId"), headers.get("session"));
        statuscode = 200;
      }
      handler.response().setStatusCode(statuscode).end();
      statuscode = 429;

    }).listen(7890);

    this.socket.binaryMessageHandler(res -> {
      System.out.println("Response from server: " + res);
      JsonObject json = (JsonObject) Json.decodeValue(res);

      //check if the message is for a single user/ip/sessions, type is "single"
      if(Objects.equals(json.getString("publishType"), "single")) {

        switch(json.getString("type")){
          case "ips":
            blockedIP.put(json.getString("identifier"), Long.parseLong(json.getString("blockedTime")));
            break;
          case "sessions":
            blockedSession.put(json.getString("identifier"), Long.parseLong(json.getString("blockedTime")));
            break;
          case "userIds":
            blockedUserId.put(json.getString("identifier"), Long.parseLong(json.getString("blockedTime")));
            break;
          default: break;
        }

      //check if the message is to update the blockedList, type is "saveState"
      }else if(Objects.equals(json.getString("publishType"), "saveState")){

        // iterate over entire list to add each value to blockList
        // Key = IP/session/userID, value = time of expiry
        for(Map.Entry<String, Object> item : json.getJsonObject("ips")){
          blockedIP.put(item.getKey(), new BigDecimal((String) item.getValue()).longValue());
        }
        // iterate over entire list to add each value to blockList
        for(Map.Entry<String, Object> item : json.getJsonObject("sessions")){
          blockedSession.put(item.getKey(), new BigDecimal((String) item.getValue()).longValue());
        }
        // iterate over entire list to add each value to blockList
        for(Map.Entry<String, Object> item : json.getJsonObject("userIds")){
          blockedUserId.put(item.getKey(), new BigDecimal((String) item.getValue()).longValue());
        }
      }
    });
  }

  // Iterate over every list and remove expired values
  // Call this periodically or every time a new user is blocked
  private void updateBlockedList(){

    blockedIP.forEach(
      (key, value) -> {

      }

    );
  }
}
