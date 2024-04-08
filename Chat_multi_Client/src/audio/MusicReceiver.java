package audio;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import javax.sound.sampled.*;

public class MusicReceiver {
    private PlayerThread playerThread;
    private DatagramSocket clientSocket;
    private int BUFFER_SIZE = 1024 + 4; // Asegúrate de que este tamaño coincida con el tamaño del paquete enviado

    public MusicReceiver(PlayerThread playerThread) throws SocketException {
        this.playerThread = playerThread;
        this.clientSocket = new DatagramSocket(6789); // El puerto debe coincidir con el puerto del servidor
    }

    public void startReceiving() {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            while (true) {
                clientSocket.receive(packet);
                buffer = packet.getData();
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                int packetCount = byteBuffer.getInt();
                if (packetCount == -1) {
                    System.out.println("Received last packet");
                    break;
                } else {
                    byte[] audioData = new byte[1024];
                    byteBuffer.get(audioData, 0, audioData.length);
                    playerThread.addBytes(audioData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientSocket.close();
        }
    }
}
