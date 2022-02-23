package webserver;

import java.io.File;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class Server {
  private ServerSocket serverSocket;

  public Server() {}

  public void listen(int port, String webrootPath) {
    try {
      var cores = Runtime.getRuntime().availableProcessors();
      var executor = Executors.newFixedThreadPool(cores);
      var path = new File(webrootPath).isAbsolute()
        ? webrootPath
        : Paths.get(System.getProperty("user.dir"), webrootPath).toString();

      serverSocket = new ServerSocket(port);
      System.out.println("Listening on port " + port);
      System.out.println();

      addShutdownHook();

      while (true) {
        executor.submit(new ConnectionHandler(serverSocket.accept(), path));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          if (serverSocket != null)
            serverSocket.close();
          System.out.println("ServerSocket closed.");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
