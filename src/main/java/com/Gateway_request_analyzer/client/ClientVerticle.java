package com.Gateway_request_analyzer.client;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;




public class ClientVerticle extends AbstractVerticle {

  private HttpClient httpClient;
  private WebSocket socket;
  @Override
  public void start() throws Exception {
    startClient(vertx);
  }

      private void startClient(Vertx vertx){
        HttpClient client = vertx.createHttpClient();

        client.webSocket(3000, "localhost", "/", websocket -> {
          if(websocket.succeeded()){

            socket = websocket.result();
            socket.writeTextMessage("Hello from client");
            socket.handler(data -> System.out.println(data.toString()));
            socket.end();

         // socket.writeBinaryMessage(buf, handler(socket));

          } else{
            System.out.println("Something went wrong" + websocket.cause().getCause());
          }
      });
    }
  }
