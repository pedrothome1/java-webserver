package webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {
  private Socket socket;
  private String webrootPath;

  /**
   * @param socket
   * @param webrootPath is expected to be an absolute path.
   */
  public ConnectionHandler(Socket socket, String webrootPath) {
    this.socket = socket;
    this.webrootPath = webrootPath;
  }

  @Override
  public void run() {
    BufferedReader in = null;
    BufferedWriter out = null;

    try {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      var requestLines = new ArrayList<String>();

      while (true) {
        var line = in.readLine();
        if (line == null || line.isEmpty())
          break;
        requestLines.add(line);
      }

      if (requestLines.isEmpty()) {
        System.out.println("The request was empty.");
        return;
      }

      System.out.println(requestLines.get(0));

      var split = requestLines.get(0).split(" ");
      var method = split[0];

      var uri = split[1].substring(1); // get rid of the /
      var filename = uri;
      if (uri.isEmpty())
        filename = "index.html";

      var filePath = Paths.get(webrootPath, filename);
      
      if (!filePath.toFile().exists()) {
        writeErrorResponse(out, "404 Not Found");
      } else if (method.equals("GET") || method.equals("HEAD")) {
        try (var fileReader = Files.newBufferedReader(filePath)) {
          out.write("HTTP/1.1 200 OK\r\n");
          out.write("Content-Type: " + Files.probeContentType(filePath) + "\r\n");
          out.write("Content-Length: " + filePath.toFile().length() + "\r\n");
          out.write("\r\n");

          if (method.equals("GET")) {
            while (true) {
              var line = fileReader.readLine();
              if (line == null)
                break;
  
              // I should read the file contents as is instead.
              out.write(line + System.lineSeparator());
            }
          }
        }
      } else {
        writeErrorResponse(out, "405 Not Allowed");
      }

      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        in.close();
        out.close();
        socket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void writeErrorResponse(BufferedWriter writer, String error) throws IOException {
    var errorPage = errorPage(error);
    writer.write("HTTP/1.1 " + error + "\r\n");
    writer.write("Content-Type: text/html\r\n");
    writer.write("Content-Length: " + errorPage.length() + "\r\n");
    writer.write("\r\n");
    writer.write(errorPage);
  }

  private static String errorPage(String title) {
    return String.format("<html><head><title>%s</title></head><body><h1>%<s</h1></body></html>", title);
  }
}
