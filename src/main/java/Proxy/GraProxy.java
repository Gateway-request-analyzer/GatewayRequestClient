package Proxy;

import Client.GraClient;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.client.WebClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;


public class GraProxy {
  private Vertx vertx;
  private HttpServer server;
  private GraClient graClient;
  private WebClient webClient;


  public GraProxy(Vertx vertx, HttpServer server, GraClient graClient){
    this.vertx = vertx;
    this.server = server;
    this.graClient = graClient;
    this.webClient = WebClient.create(vertx);
    this.setUpHandlers();
    // TODO: read config file and save in datatype to check incoming requests
  }

  private void setUpHandlers(){


    this.server.requestHandler(handler ->{

      // TODO: check config-file/datatype that the customer app is allowed to use product here

      graClient.updateBlockedList();

      MultiMap headers = handler.headers();
      HttpMethod method = handler.method();
      String ip = headers.get("ip_address");
      String session = headers.get("session");
      String userId = headers.get("userId");

      String uri = handler.absoluteURI();
      URI endPoint = null;
      try {
        endPoint = proxiedEndPoint(uri);
      } catch (URISyntaxException | MalformedURLException e) {
        System.out.println("Error in parsing the following URI: " + uri);
        System.out.println("With the following exception: " + e.getMessage());
      }

      System.out.println("URL from header: " + uri);
      if(graClient.checkBlockedList(ip, session, userId)){
        // TODO: Add challenge here if user needs to be verified?
        // TODO: Respond with appropriate body/status code for verification

        if(endPoint != null) {
          graClient.sendEvent(headers.get("ip_address"), headers.get("userId"), headers.get("session"), endPoint.getPath());


          // fetch public API and return data as response
          this.proxyEndpointFetch(responseBody -> {
            handler.response().setStatusCode(200).end(responseBody);
          }, onFailure -> {
            System.out.println(onFailure);
            handler.response().setStatusCode(400).end(onFailure);
          }, endPoint, headers, method);
          // statusCode = 200;
        }
      } else {
        System.out.println("This user is currently blocked: " + ip);
        handler.response().setStatusCode(429).end("This user is currently blocked");
      }
    }).listen(7890);
    System.out.println("handlers set up");
  }

  private void proxyEndpointFetch(Consumer<Buffer> responseBody, Consumer<String> onFailure, URI uri, MultiMap headers, HttpMethod method) {

      System.out.println("Headers on request:");
      System.out.println(headers);

      this.webClient
        .requestAbs(method, String.valueOf(uri))
        .send()
        .onSuccess(handler -> {

          System.out.println(handler.statusCode());
          System.out.println("Message body received: " + handler.body().toString());
          responseBody.accept(handler.body());

        }).onFailure(err -> {
          onFailure.accept("Error fetching API: " + err.getMessage());
          System.out.println("Error fetching API: " + err.getMessage());
        });

    }



  private URI proxiedEndPoint(String uri) throws URISyntaxException, MalformedURLException {

      // $GRA_GATEWAY/localhost:8081/breeds"
      URL oldURL = new URL(uri);
      String host = oldURL.getPath().split("/")[1];
      String path = oldURL.getPath().replace("/" + host, "");
      // TODO: Look in config for correct protocol
      // TODO: Vet bas-url från kund
      return new URI(oldURL.getProtocol(), host, path, oldURL.getQuery(), oldURL.getRef());

  }

}
