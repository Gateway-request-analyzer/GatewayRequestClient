package com.Gateway_request_analyzer.client;

import io.vertx.core.AbstractVerticle;

import java.util.Random;

public class DepVerticle extends AbstractVerticle {


  @Override
  public void start() throws Exception{
    //amounts of clients started
    for(int i = 0; i < 3; i++){
      vertx.deployVerticle(new ClientVerticle());
    }
  }
}
