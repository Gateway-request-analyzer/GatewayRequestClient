package Client;

import io.vertx.core.AbstractVerticle;

/**
 * Class used solely for testing. Creates requested amount of clients.
 */
public class DepVerticle extends AbstractVerticle {


  @Override
  public void start() throws Exception{
    //amounts of clients started
    for(int i = 0; i < 1; i++){
      vertx.deployVerticle(new ClientVerticle());
    }
  }
}
