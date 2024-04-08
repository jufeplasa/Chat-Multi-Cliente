package audio;

// AudioClient.java
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class AudioClient {
    private Socket socket;
    private InputStream in;
    private AudioFormat format;
    private SourceDataLine line;

    public AudioClient(String host, int port) throws IOException, LineUnavailableException {
        socket = new Socket(host, port);
        in = socket.getInputStream();
        format = new AudioFormat(88000.0f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
    }

    public void play() {
        try {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                line.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            line.drain();
            line.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, LineUnavailableException {
        AudioClient client = new AudioClient("127.0.0.1", 9876);
        client.play();
    }
}
