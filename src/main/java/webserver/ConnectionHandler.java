package webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
    try (
      var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    ) {
      var requestLines = new ArrayList<String>();

      while (true) {
        var line = in.readLine();
        if (line == null || line.isEmpty())
          break;
        requestLines.add(line);
      }

      System.out.println(requestLines.get(0));

      var split = requestLines.get(0).split(" ");
      
      if (split[0].equals("GET")) {
        var uri = split[1].substring(1);
        var filename = uri;
        if (uri.isEmpty())
          filename = "index.html";

        var filepath = Paths.get(webrootPath, filename).toString();
        var filepathSplit = filepath.split("\\.");
        var fileExt = filepathSplit[filepathSplit.length - 1];
        var file = new File(filepath);

        try (var fileReader = new BufferedReader(new FileReader(file))) {
          out.write("HTTP/1.1 200 OK\r\n");
          out.write("Content-Type: " + getContentType(fileExt) + "\r\n");
          out.write("Content-Length: " + file.length() + "\r\n");
          out.write("\r\n");

          while (true) {
            var line = fileReader.readLine();
            if (line == null)
              break;

            out.write(line + System.lineSeparator());
          }
        }

        out.flush();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String getContentType(String fileExtension) {
    switch (fileExtension) {
    case "html":
      return "text/html";
    case "css":
      return "text/css";
    case "js":
      return "text/javascript";
    case "txt":
    default:
      return "text/plain";
    }
  }
}
