package Client;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class clientBufferHelper {
  private ArrayList<JsonObject> buffer;

  public clientBufferHelper(){
    this.buffer = new ArrayList<JsonObject>();
  }


  public void addElement(JsonObject obj){
    if(!this.isBufferFull()){
      this.buffer.add(obj);
    } else {
      System.out.println("Buffer currently full");
    }
  }

  public ArrayList<JsonObject> getBuffer(){
    return this.buffer;
  }

  private boolean isBufferFull(){
      if(this.buffer.size() > 3000){
        return true;
      }
        return false;
  }

  public void resetBuffer(){
    this.buffer = new ArrayList<JsonObject>();
  }

}
