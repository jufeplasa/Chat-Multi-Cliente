package audio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class PlayerThread extends Thread {
    private static int MAX_ITEMS_IN_QUEUE = 3;
    private int secondsBuffer = 280;
    BlockingQueue<byte[]> buffer;
    private SourceDataLine sourceDataLine;
    private int count = 0;
    private int packets = 0;

    public PlayerThread(AudioFormat audioFormat, int BUFFER_SIZE) {
        try {
            MAX_ITEMS_IN_QUEUE = (int) audioFormat.getSampleRate() * secondsBuffer *
                    audioFormat.getFrameSize() / BUFFER_SIZE;

            System.out.println("Max items in queue: " + MAX_ITEMS_IN_QUEUE);
            buffer = new ArrayBlockingQueue<>(MAX_ITEMS_IN_QUEUE, true);
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBytes(byte[] bytes) {
        try {
            count++;
            buffer.put(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                if (buffer.isEmpty()) {
                    if (packets > 0) {
                        System.out.println("Packets: write " + packets + " add Count: " + count);
                        packets = 0;
                        count = 0;
                    }
                    Thread.yield();
                    continue;
                }
                byte[] bytes = buffer.take();
                packets++;
                sourceDataLine.write(bytes, 0, bytes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
