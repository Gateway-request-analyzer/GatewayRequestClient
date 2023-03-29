package Client;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.net.ConnectException;
import java.util.*;
import java.util.function.Consumer;


public class GraClient {

  public Vertx vertx;
  public WebSocket socket;
  private HttpServer server;
  private boolean serverRunning;
  private long timerDelay;

  //list of blocked users/ip/sessions

  private HashMap<String, Long> blockedIP = new HashMap<>();
  private HashMap<String, Long> blockedSession = new HashMap<>();
  private HashMap<String, Long> blockedUserId = new HashMap<>();

  public GraClient(Vertx vertx, WebSocket socket) {
    this.vertx = vertx;
    this.socket = socket;
    setUpHandlers();
    this.serverRunning = true;
  }

  public void sendEvent(String ip, String userId, String session, String accessToken){

    if(serverRunning){

        JsonObject jo = new JsonObject();
        jo.put("ip", ip).put("userId", userId).put("session", session).put("Authorization", accessToken);

        Buffer json = Json.encodeToBuffer(jo);
        socket.writeBinaryMessage(json).onFailure(e -> {
          throw new RuntimeException("failed to send event");
        }).onSuccess(handler -> {
          System.out.println("Message sent");
        });

    } else {
      System.out.println("Server not running, attempting to reconnect");
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

    this.socket.exceptionHandler(handler -> {
      System.out.println("exceptionHandler invoked");

      this.serverRunning = false;
      this.socketReconnect(3);

    });

    this.socket.binaryMessageHandler(res -> {

      //System.out.println("Response from server: " + res);
      JsonObject json = (JsonObject) Json.decodeValue(res);

      //check if the message is for a single user/ip/sessions, type is "single"
      if(Objects.equals(json.getString("publishType"), "single")) {

        switch(json.getString("actionType")){
          case "blockedByIp":
            blockedIP.put(json.getString("value"), Long.parseLong(json.getString("blockedTime")));
            break;
          case "blockedBySession":
            blockedSession.put(json.getString("value"), Long.parseLong(json.getString("blockedTime")));
            break;
          case "blockedByUserId":
            blockedUserId.put(json.getString("value"), Long.parseLong(json.getString("blockedTime")));
            break;
          default: break;
        }

      //check if the message is to update the blockedList, type is "saveState"
      }else if(Objects.equals(json.getString("publishType"), "saveState")){

        // iterate over entire list to add each value to blockList
        // Key = IP/session/userID, value = time of expiry
        System.out.println("Response from server: " + json);
        json.remove("publishType");

        // {"publishType":"saveState","1.2.3.4":{"source":"rateLimiter","actionType":"blockedByIp","blockedTime":"1675426181182","value":"1.2.3.4","publishType":"single"},"abc123":{"source":"rateLimiter","actionType":"blockedByUserId","blockedTime":"1675426181186","value":"abc123","publishType":"single"},"abcdef":{"source":"rateLimiter","actionType":"blockedBySession","blockedTime":"1675426181184","value":"abcdef","publishType":"single"}}
        for(Map.Entry<String, Object> item : json){

          System.out.println("Objects from saveState: ");
          System.out.println(item.getValue());
          JsonObject currentJson = new JsonObject(String.valueOf(item.getValue()));

          switch(currentJson.getString("actionType")){
            case "blockedByIp":
              blockedIP.put(currentJson.getString("value"), Long.parseLong(currentJson.getString("blockedTime")));
              break;
            case "blockedBySession":
              blockedSession.put(currentJson.getString("value"), Long.parseLong(currentJson.getString("blockedTime")));
              break;
            case "blockedByUserId":
              blockedUserId.put(currentJson.getString("value"), Long.parseLong(currentJson.getString("blockedTime")));
              break;
            default:
              break;
          }
        }
      }
    });
  }

  // Iterate over every list and remove expired values
  // Call this periodically or every time a new user is blocked
  public void updateBlockedList(){
    blockListHelper(blockedIP);
    blockListHelper(blockedSession);
    blockListHelper(blockedUserId);
  }

  private void blockListHelper(HashMap<String, Long> currentList){
    long currentTime = System.currentTimeMillis();
    currentList.entrySet().removeIf(entry -> entry.getValue() < currentTime);
  }

  public boolean checkBlockedList(String ip, String session, String userId){
    return (!blockedIP.containsKey(ip) && !blockedUserId.containsKey(userId) && !blockedSession.containsKey(session));
  }


  private void socketReconnect(long delay){
    if(delay > 60){
      delay = 60;
    }
    timerDelay = delay;

    this.vertx.createHttpClient().webSocket(3000, "localhost", "/")
      .onSuccess(socket -> {

        this.socket = socket;
        this.serverRunning = true;

      }).onFailure(e -> {

        this.vertx.setTimer(timerDelay*1000, handler -> {
          this.socketReconnect(timerDelay*2);
        });

      });
  }


}
