package Testing;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;


public class TestingEndpoint extends AbstractVerticle {

  @Override
  public void start(){
    vertx.createHttpServer().requestHandler(handler -> {
        String uri = handler.absoluteURI();
        handler.response().setStatusCode(200).end(new JsonObject().put("RequestURI", uri).toBuffer());
    }).listen(8081);
  }
}
