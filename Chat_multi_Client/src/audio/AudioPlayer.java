package audio;

import javax.sound.sampled.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AudioPlayer {
    private DatagramSocket socket;
    private AudioFormat format;
    private SourceDataLine sourceLine;
    private int UDP_PORT = 9876;

    public AudioPlayer() throws LineUnavailableException {
        // Configura el formato de audio (asegúrate de que coincida con el formato de
        // grabación)
        format = new AudioFormat(16000.0f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(format);
        sourceLine.start();
    }

    public void startReceiving() {
        try {
            socket = new DatagramSocket(UDP_PORT); // El puerto debe coincidir con el puerto del servidor
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            while (true) {
                socket.receive(receivePacket);
                // Extrae los datos de audio del paquete y los escribe en la línea de fuente
                // para reproducir
                byte[] audioData = receivePacket.getData();
                sourceLine.write(audioData, 0, audioData.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sourceLine.drain();
            sourceLine.close();
            socket.close();
        }
    }
}