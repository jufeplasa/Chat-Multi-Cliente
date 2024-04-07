package chat.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Player extends Thread {
    private static int MAX_ITEMS_IN_QUEUE = 3;
    private int secondsBuffer = 100;
    BlockingQueue<byte[]> buffer;
    private SourceDataLine sourceDataLine;
    private int count = 0;
    private int packes = 0;

    public Player(AudioFormat audioFormat, int BUFFER_SIZE) {
        // BUFFER_SIZE es un entero que determina el tamanio del buffer, un valor normal
        // debe ser 1028
        try {
            MAX_ITEMS_IN_QUEUE = (int) audioFormat.getSampleRate() * secondsBuffer *
                    audioFormat.getFrameSize()
                    / BUFFER_SIZE;

            System.out.println("Max items in queue: " + MAX_ITEMS_IN_QUEUE);
            buffer = new ArrayBlockingQueue<>(MAX_ITEMS_IN_QUEUE, true);
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // poner bytes en la cola, esta cola es la que finalmente se va a reproducir
    public void addBytes(byte[] bytes) {
        try {
            count++;
            buffer.put(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // en ejecucion
    public void run() {
        while (true) {
            try {
                if (buffer.isEmpty()) {
                    if (packes > 0) {
                        System.out.println("Packets: write " + packes + " add Count: " + count);
                        packes = 0;
                        count = 0;
                    }
                    Thread.yield();
                    continue;
                }
                // toma los bytes almacenados en la cola y elimina esa informacion de la cola.
                // Espera si es necesario
                byte[] bytes = buffer.take();
                packes++;
                // pone los bytes leidos en la tarjeta de sonido
                sourceDataLine.write(bytes, 0, bytes.length);
                // System.out.println("Written " + w + " bytes to sound card. " +
                // buffer.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
