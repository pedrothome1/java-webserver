package webserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
      var line = in.readLine();

      if (line == null) {
        out.flush();
        return;
      }

      while (line.isBlank())
        line = in.readLine();
      
      while (!in.readLine().isBlank());

      var method = line.substring(0, line.indexOf(' '));
      var uri = line
        .substring(line.indexOf('/'), line.lastIndexOf(' '))
        .substring(1)
        .replace("/", File.separator)
        .trim();

      var fileName = uri.isEmpty() ? "index.html" : uri;
      var file = Paths.get(webrootPath, fileName).toFile();

      if (file.isDirectory()) {
        fileName = "index.html";
        file = Paths.get(file.getAbsolutePath(), fileName).toFile();
      }

      if (!file.exists()) {
        var reason = "404 Not Found";
        out.write("HTTP/1.1 " + reason + "\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("Content-Length: " + reason.length() + "\r\n");
        out.write("\r\n");
        out.write(reason);
        out.flush();
        
        return;
      }

      try (var reader = new BufferedInputStream(new FileInputStream(file.getPath()))) {
        out.write("HTTP/1.1 200 OK\r\n");
        out.write("Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n");
        out.write("Content-Length: " + file.length() + "\r\n");
        out.write("\r\n");
        
        int b;
        if (method.equals("GET"))
          while ((b = reader.read()) != -1)
            out.write(b);
        
        out.flush();
      }
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
