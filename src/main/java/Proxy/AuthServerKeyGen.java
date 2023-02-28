package Proxy;

import java.io.IOException;

public class AuthServerKeyGen {

  public AuthServerKeyGen(){
    System.out.println("reached keygen");
    generateKeys();

  }

  public void generateKeys() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    try {
      processBuilder.command("./generateKeys.sh").start();

    }catch (IOException e) {
      System.out.println("failed to run scrept: " + e.getCause());
    }
    System.out.println("Keys generated!");
  }
}
