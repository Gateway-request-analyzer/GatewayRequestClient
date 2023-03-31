package Client;
import io.vertx.core.json.JsonObject;

import java.util.LinkedList;

public class clientBufferHelper {
  private LinkedList<JsonObject> buffer;

  public clientBufferHelper(){
    this.buffer = new LinkedList<JsonObject>();
  }

  public void addElement(JsonObject obj){
    if(!this.bufferController()){
      this.buffer.add(obj);
    } else {
      System.out.println("Buffer currently full");
    }
  }

  public LinkedList<JsonObject> getBuffer(){
    return this.buffer;
  }

  private boolean bufferController(){
      if(this.buffer.size() > 15){
        return true;
      }
        return false;
  }

  public void resetBuffer(){
    this.buffer = new LinkedList<JsonObject>();
  }

}
