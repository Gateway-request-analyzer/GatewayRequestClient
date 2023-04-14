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

  private AuthClient authClient;
  private int statusCode;

  public GraProxy(Vertx vertx, HttpServer server, GraClient graClient, AuthClient authClient){
    this.vertx = vertx;
    this.server = server;
    this.graClient = graClient;
    this.authClient = authClient;
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


      System.out.println("URL from header: " + uri);
      if(graClient.checkBlockedList(ip, session, userId)){

        // TODO: skicka token som en egen variabel istället för att sno session platsen
          graClient.sendEvent(headers.get("ip_address"), headers.get("userId"), session, authClient.getToken());


        // fetch public API and return data as response
        this.proxyEndpointFetch(responseBody -> {
          handler.response().setStatusCode(200).end(responseBody);
        }, onFailure -> {
          System.out.println(onFailure);
          handler.response().setStatusCode(400).end(onFailure);
        }, uri, headers, method);
       // statusCode = 200;
      } else {
        System.out.println("This user is currently blocked: " + ip);
        statusCode = 429;
        handler.response().setStatusCode(429).end("This user is currently blocked");
      }
    }).listen(7890);
    System.out.println("handlers set up");
  }

  private void proxyEndpointFetch(Consumer<Buffer> responseBody, Consumer<String> onFailure, String uri, MultiMap headers, HttpMethod method) {

    /**
     * TODO:
     * Om det är Post request måste all data skickas med.
     * Headers måste skickas med.
     * JWT token måste också autentiseras här utöver GRAclient.
     * Borde gå att kolla på URL-delen efter domänen, hitta aktuell funktion. Domänen kan förändras. (Split on first slash)
     */
    try {
      // $GRA_GATEWAY/catfact.ninja/breeds"
      URL oldURL = new URL(uri);
      String host = oldURL.getPath().split("/")[1];
      String path = oldURL.getPath().replace("/" + host, "");

      // TODO: Look in config for correct protocol
      // TODO: Vet bas-url från kund

      URI newURL =
        new URI
          (oldURL.getProtocol(), host, path, oldURL.getQuery(), oldURL.getRef());

      System.out.println(" ");
      System.out.println(newURL);
      System.out.println("scheme: " + newURL.getScheme());
      System.out.println("user info: " + newURL.getUserInfo());
      System.out.println("host: " + newURL.getHost());
      System.out.println("path: " + newURL.getPath());
      System.out.println("query: " + newURL.getQuery());
      System.out.println("Fragment/ref: " + newURL.getFragment());
      System.out.println("Full URL: " + newURL);

      System.out.println(" ");
      System.out.println("Headers on request:");
      System.out.println(headers);


      this.webClient
        .requestAbs(method, String.valueOf(newURL))
        .send()
        .onSuccess(handler -> {

          System.out.println(handler.statusCode());
          System.out.println("Message body received: " + handler.body().toString());
          responseBody.accept(handler.body());

        }).onFailure(err -> {
          onFailure.accept("Error fetching API: " + err.getMessage());
          System.out.println("Error fetching API: " + err.getMessage());
        });

    } catch (MalformedURLException | URISyntaxException e) {
      System.out.println("Error parsing URL" + e.getMessage());
    }


/*  Old way
    String[] prelString = uri.split("/", 4);
    String actualURL = "http://" + prelString[3];
    System.out.println("Old chopped string: " + actualURL);
*/

  }


}
