package audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Record {

    private Thread recordingThread;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private Thread stopper;
    private boolean recording;
    private int recordingNumber;

    public Record() {
        recordingNumber = loadRecordingNumber(); // Cargar el número de grabación desde el archivo
    }

    // Método para cargar el número de grabación desde un archivo
    private int loadRecordingNumber() {
        try (BufferedReader reader = new BufferedReader(new FileReader("recording_number.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0; // Valor predeterminado si no se puede cargar el número de grabación
    }

    // Método para guardar el número de grabación en un archivo
    private void saveRecordingNumber(int number) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("recording_number.txt"))) {
            writer.write(Integer.toString(number));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para iniciar la grabación de audio
    public void startRecording() {
        try {
            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            recording = true;
            recordingNumber++; // Incrementar el número de grabaciones
            saveRecordingNumber(recordingNumber);
            System.out.println("Grabación iniciada. Presiona 'Enter' para detener.");
            AudioInputStream audioStream = new AudioInputStream(targetDataLine);

            // Nuevo hilo para esperar la presión de una tecla
            stopper = new Thread(() -> {
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stopRecording();
            });

            stopper.start();

            // Escribe los datos de audio a un archivo
            File audioDir = new File("audios");
            if (!audioDir.exists()) {
                audioDir.mkdir(); // Crear la carpeta si no existe
            }
            File audioFile = new File(audioDir, "grabacion" + recordingNumber + ".wav");
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        recording = false;
        targetDataLine.stop();
        targetDataLine.close();
        System.out.println("Grabación detenida.");
    }

    public boolean getRecord() {
        return this.recording;
    }

}
