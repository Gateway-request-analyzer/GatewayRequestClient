package Proxy;

import Client.GraClient;
import io.vertx.core.*;
import io.vertx.core.http.*;

import java.util.Timer;
import java.util.TimerTask;


public class ProxyVerticle extends AbstractVerticle {

  private HttpClient httpClient;
  private WebSocket socket;
  private Timer timer;
  private TimerTask tt;
  long timerDelay;
  int reconCounter;
  private GraClient client;
  private GraProxy proxy;


  @Override
  public void start() {


  this.clientSetup();


  }

  private void clientSetup(){

    this.vertx.createHttpClient().webSocket(3000, "localhost", "/")
      .onComplete(socket -> {
        HttpServer server = this.vertx.createHttpServer();
        this.client = new GraClient(vertx, socket.result());
        this.proxy = new GraProxy(vertx, server, client);
        socket.result().exceptionHandler(handler -> {
            this.socketReconnect(2);
        });
      });
  }

  private void socketReconnect(long delay){
    if(delay > 60){
      delay = 60;
    }
    timerDelay = delay;

    this.vertx.createHttpClient().webSocket(3000, "localhost", "/")
      .onSuccess(socket -> {

      client.setSocket(socket);
      proxy.setConnectionStatus(true);

      }).onFailure(e -> {

       this.vertx.setTimer(timerDelay*1000, handler -> {
          this.socketReconnect(timerDelay*2);
       });

      });

  }




}

