package com.Gateway_request_analyzer.client;


public class Event {
  private String ip, userId, session, URI;

  public Event(String ip, String userId, String session, String URI){
    this.ip = ip;
    this.userId = userId;
    this.session = session;
    this.URI = URI;
  }
}
