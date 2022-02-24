package webserver;

import java.io.File;

public class Program {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("The webroot path is required.");
      System.exit(1);
    }

    if (!new File(args[0]).isDirectory()) {
      System.err.println("The webroot path must be a directory.");
      System.exit(1);
    }

    var server = new Server();
    server.listen(8090, args[0]);
  }
}
