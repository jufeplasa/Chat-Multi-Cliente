package audio;

// AudioServer.java
import java.io.*;
import java.net.*;

public class AudioServer {
    private ServerSocket serverSocket;

    public AudioServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                    OutputStream out = clientSocket.getOutputStream();
                    FileInputStream fis = new FileInputStream("audios/grabacion1.wav")) { // Esto debe entrtar como
                                                                                          // parametro

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        AudioServer server = new AudioServer(9876);
        server.start();
    }
}
