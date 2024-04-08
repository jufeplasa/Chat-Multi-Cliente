package chat.client;

// PlayerSender.java
import java.io.File;
import java.io.IOException;
import java.net.*;
import javax.sound.sampled.*;

public class PlayerSender {
    private AudioInputStream audioStream;
    private DatagramSocket socket;
    private InetAddress ipAddress;
    private int port;

    public PlayerSender(String audioFilePath, InetAddress ipAddress, int port)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.audioStream = AudioSystem.getAudioInputStream(new File(audioFilePath));
        this.socket = new DatagramSocket();
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void sendAudio() throws IOException {
        byte[] audioBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = audioStream.read(audioBuffer)) != -1) {
            DatagramPacket packet = new DatagramPacket(audioBuffer, bytesRead, ipAddress, port);
            socket.send(packet);
        }
        socket.close();
        audioStream.close();
    }
}
