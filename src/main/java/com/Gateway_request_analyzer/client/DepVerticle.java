package com.Gateway_request_analyzer.client;

import io.vertx.core.AbstractVerticle;

import java.util.Random;

public class DepVerticle extends AbstractVerticle {


  @Override
  public void start() throws Exception{
    for(int i = 0; i < 10; i++){
      vertx.deployVerticle(new ClientVerticle());
    }
  }
}
