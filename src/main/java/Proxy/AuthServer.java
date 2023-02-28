package Proxy;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

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
import java.util.Objects;

public class AuthServer {

  RSAPrivateKey privateKey;
  RSAPublicKey publicKey;
  HttpServer server;

  Vertx vertx;

  public AuthServer(HttpServer server, Vertx vertx){
    System.out.println("reached authserver");
    //AuthServerKeyGen genKeys = new AuthServerKeyGen();
    this.server = server;
    this.vertx = vertx;
    setUpHandlers(server);
  }

  // TODO: Generate token and return it to requester
  // Använd client credential, reject everything else
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
      request.response().putHeader("Content-Type", "application/json;charset=UTF-8")
        .putHeader("Cache-Control", "no-store")
        .putHeader("Pragma", "no-cache")
        .setStatusCode(200).end(payload.toBuffer());
    } catch (JWTCreationException exception){
      System.out.println("failed to create exception: " + exception.getMessage());
      // Invalid Signing configuration / Couldn't convert Claims.
    }

  }

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

    server.requestHandler(request -> {
      // TODO: If a valid grant is not offered, Do not generate token

      System.out.println("These are the requestheaders: " + request.headers());
      MultiMap headers = request.headers();
      if(Objects.equals(headers.get("Authorization"), "Basic PGNsaWVudC1pZD46PGNsaWVudC1zZWNyZXQ+")) {
        generateToken(request);
      }else{
        request.response().setStatusCode(318).end("Invalid claim");
      }


    }).listen(8888);

  }

}
