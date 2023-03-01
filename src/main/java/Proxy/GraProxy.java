package Proxy;

import Client.GraClient;
import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;


public class GraProxy {
  private Vertx vertx;
  private HttpServer server;
  private GraClient internalClient;
  private WebClient webClient;

  private AuthClient authClient;
  private int statusCode;

  public GraProxy(Vertx vertx, HttpServer server, GraClient internalClient, AuthClient authClient){
    this.vertx = vertx;
    this.server = server;
    this.internalClient = internalClient;
    this.authClient = authClient;
    this.webClient = WebClient.create(vertx);
    this.setUpHandlers();
    /**
     * TODO:
     * Read config from file to
     */
  }

  private void setUpHandlers(){


    this.server.requestHandler(handler ->{

      internalClient.updateBlockedList();

      MultiMap headers = handler.headers();
      String ip = headers.get("ip_address");
      String session = headers.get("session");
      String userId = headers.get("userId");
      //Vi vill nog inte kolla på headern här, se funktionen nedan. Kan även vara att portar etc följer med. Hitta andra URI-funktioner.
      //String URL = headers.get("RequestURL");

      String URL = handler.absoluteURI();
      String reqMethod = headers.get("RequestMethod");
      System.out.println("URL from header: " + URL);
      if(internalClient.checkBlockedList(ip, session, userId)){

        // TODO: skicka med korrekt JWT
          internalClient.sendEvent(headers.get("ip_address"), headers.get("userId"), authClient.getToken());


        // TODO: fetch public API and return data as response
        this.proxyEndpointFetch(URL, reqMethod, handler);
       // statusCode = 200;
      } else {
        System.out.println("This user is currently blocked: " + ip);
        statusCode = 429;
        handler.response().setStatusCode(429).end("This user is currently blocked");
      }
    }).listen(7890);
    System.out.println("handlers set up");
  }

  private void proxyEndpointFetch(String URL, String reqMethod, HttpServerRequest request){

    /**
     * TODO:
     * Om det är Post request måste all data skickas med.
     * Headers måste skickas med.
     * JWT token måste också autentiseras här utöver GRAclient.
     * Borde gå att kolla på URL-delen efter domänen, hitta aktuell funktion. Domänen kan förändras. (Split on first slash)
     */
    String[] prelString = URL.split("/", 4);
    String actualURL = "http://" + prelString[3];

    this.webClient
      .getAbs(actualURL)
      .send()
      .onSuccess(handler -> {
        System.out.println("Message body received: " + handler.body().toString());
        request.response().setStatusCode(200).end(handler.body());

      }).onFailure(err -> {
        System.out.println("Error checking cat breeds: " + err.getMessage());
      });
  }


}
