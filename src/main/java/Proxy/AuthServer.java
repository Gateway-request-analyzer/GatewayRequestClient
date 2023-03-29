package Proxy;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.impl.OAuth2API;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class AuthServer {

  RSAPrivateKey privateKey;
  RSAPublicKey publicKey;
  HttpServer server;

  HashSet<String> clients = new HashSet<>();

  Vertx vertx;

  public AuthServer(HttpServer server, Vertx vertx){

    // TODO: kolla upp yaml och hur en legit config fil bör se ut
    // TODO: samt helper funktion för att hämta config

    // Assuming these are kept in a file in reality
    clients.add("Basic " + Base64.getEncoder().encodeToString("client1:secret123".getBytes()));
    clients.add("Basic " + Base64.getEncoder().encodeToString("client2:secret124".getBytes()));

    // TODO: kolla upp exakt hur ofta nycklar ska genereras och hur den klassen bör se ut
    //AuthServerKeyGen genKeys = new AuthServerKeyGen();

    this.server = server;
    this.vertx = vertx;
    setUpHandlers(server);
  }

  private void generateToken(HttpServerRequest request){

    try {
      getKeys();
    } catch (Exception e){
      System.out.println("Failed to load keys from file: " + e.getMessage());
      return;
    }

    try {
      Algorithm algorithm = Algorithm.RSA256(this.privateKey);
      String token = JWT.create()
        .withExpiresAt(new Date(System.currentTimeMillis() + 300000))
        .sign(algorithm);

      JsonObject payload = new JsonObject();
      payload.put("access_token", token);
      payload.put("token_type", "jwt");
      payload.put("expires_in", "300");
      payload.put("refresh_token", "<refresh token>");

      request.response()
        .putHeader("Content-Type", "application/json;charset=UTF-8")
        .putHeader("Cache-Control", "no-store")
        .putHeader("Pragma", "no-cache")
        .setStatusCode(200).end(payload.toBuffer());

    } catch (JWTCreationException exception){
      System.out.println("failed to create exception: " + exception.getMessage());
      // Invalid Signing configuration / Couldn't convert Claims.
    }

  }

  // TODO: fundera på ifall det finns ett snyggare sätt att läsa nycklarna från fil
  public void getKeys () throws NoSuchAlgorithmException, URISyntaxException, IOException, InvalidKeySpecException {
    String privateKeyContent = new String(Files.readAllBytes(Paths.get("private_key_pkcs8.pem")));
    String publicKeyContent = new String(Files.readAllBytes(Paths.get("public_key.pem")));

    privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
    publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");;

    KeyFactory kf = KeyFactory.getInstance("RSA");

    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
    RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpecPKCS8);

    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

    this.privateKey = privKey;
    this.publicKey = pubKey;

  }

  public void setUpHandlers(HttpServer server){

    server.requestHandler(req -> {
      System.out.println("abs uri: " + req.absoluteURI());

      //Check that client id and client secret, path and method is ok
      if (req.method() == HttpMethod.POST && "/oauth/token".equals(req.path())
        && this.clients.contains(req.getHeader("Authorization"))) {
        req.setExpectMultipart(true).bodyHandler(buffer -> {


          // How to check if request is correct type, in case of refresh tokens
          System.out.println("buffer printed: " + buffer);

          if(buffer.toString().equals("grant_type=client_credentials")){
            System.out.println("Headers looks like:");
            System.out.println(req.headers());


            // Generate token and return it to client
            generateToken(req);

          }else {
            req.response().setStatusCode(400).end("no valid grant_type");
          }
        });
      } else {
        req.response().setStatusCode(400).end("no valid claim, method or url");
      }

    }).listen(8888);
  }
}
