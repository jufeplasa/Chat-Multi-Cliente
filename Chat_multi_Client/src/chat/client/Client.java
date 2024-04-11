package chat.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.sound.sampled.*;
import audio.Record;
import audio.AudioClient;
import audio.AudioPlayer;
import audio.AudioServer;
import chat.server.Chatters;
import chat.server.ClientHandler;

public class Client extends Thread {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6789;
    static Scanner sc = new Scanner(System.in);
    static ClientHandler clientHandler;
    static Record record;
    static AudioPlayer player;
    static AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
    static final int UDP_PORT = 9876; // Puerto UDP para la recepción de audio

    public static void main(String[] args) throws IOException, InterruptedException {
        try {

            // Definicion socket del cliente

            Socket clientSocket = new Socket(SERVER_IP, PORT);
            clientSocket.setSoTimeout(5000);// Espera de 5 segundos para respuesta del
            // server
            System.out.println("Conectado al servidor.");
            // Lector
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Lector del socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Escritor
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Recoder
            record = new Record();

            Thread receiveThread = new Thread(() -> {
                startServer();
            });
            receiveThread.start();

            // Player
            try {
                player = new AudioPlayer();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

            String name;
            String mensajeServer;

            do {
                // solicitar al usuario un alias, o nombre
                System.out.println("espero mensaje");
                mensajeServer = in.readLine();
                System.out.println("recibo el mensaje");
                System.out.print(mensajeServer);

                // Escribir y enviarlo al servidor
                name = userInput.readLine();
                out.println(name);

                // Respuesta del cliente.
                mensajeServer = in.readLine();
                System.out.println(mensajeServer);
                // no debe salir de este bloque hasta que el nombre no sea aceptado
            } while (mensajeServer == "Aceptado");

            // creamos el objeto Lector e iniciamos el hilo que nos permitira estar atentos
            // a los mensajes
            // que llegan del servidor
            Thread readerThread = new Thread(() -> {
                Lector lector = new Lector(in);
                lector.start(); // Iniciar el hilo de lectura
            });
            readerThread.start();
            boolean isOn = true;

            do {
                // estar atento a la entrada del usuario para poner los mensajes en el canal de
                // salida out
                System.out.println(showMenu());// Mostramos el menu
                int option = sc.nextInt();
                sc.nextLine();
                String msj = "";
                String nameGroup = "";
                switch (option) {
                    case 1:
                        System.out.print("Ingrese el nombre del grupo: ");
                        nameGroup = userInput.readLine();
                        // Enviar solicitud al servidor
                        out.println("REQUEST_CREATEGROUP " + nameGroup);
                        Thread.sleep(500);
                        // Recibir respuesta del servidor
                        msj = in.readLine();
                        // Mostrar la respuesta del servidor
                        System.out.println("Mensaje del servidor: " + msj);
                        break;
                    case 2:
                        System.out.println("Ingrese el nombre del grupo al que desea unirse: ");
                        nameGroup = userInput.readLine();
                        out.println("REQUEST_JOINGROUP " + nameGroup);// Request para entrar a grupo
                        msj = in.readLine(); // Respuesta del server
                        System.out.println("Mensaje del servidor: " + msj);
                        break;
                    case 3:
                        System.out.println("Ingrese el nombre del grupo al que desea enviar un mensaje: ");
                        nameGroup = userInput.readLine();
                        System.out.println("Ingrese el mensaje que quiere mandar: ");
                        String msjToSend = userInput.readLine();
                        out.println("REQUEST_SENDGROUPMSG " + nameGroup + " " + msjToSend);
                        msj = in.readLine(); // Respuesta del server
                        System.out.println("Mensaje del servidor: " + msj);
                        break;
                    case 4:
                        System.out.println("Escriba el nombre de la persona: ");
                        String namePerson = userInput.readLine();
                        System.out.println("Message: ");
                        String privateMsj = userInput.readLine();
                        out.println("REQUEST_SENDPRIVATEMSG " + namePerson + " " + privateMsj);// Request para
                                                                                               // enviar msj privado
                        break;
                    case 5:
                        System.out.println("Escriba el nombre del grupo: ");
                        String groupName = userInput.readLine();
                        out.println("REQUEST_HISTORY " + groupName);
                        msj = in.readLine(); // Respuesta del servidor
                        System.out.println(msj);
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:

                        break;
                    case 10:
                        if (!record.getRecord()) {
                            record.startRecording();
                        } else {
                            System.out.println("La grabación ya está en curso."); // Esto debería ir en los grupos
                                                                                  // en un menu de grupo
                        }
                        break;

                    case 0:
                        System.exit(0);
                        isOn = false;
                        break;

                    default:
                        break;
                }

            } while (isOn);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String showMenu() {
        String msj = "";
        msj = ("------------Menu desde chat------------- \n" +
                "1. Crear grupo \n" +
                "2. Ingresar a un grupo \n" +
                "3. Mandar un mensaje a un grupo \n" +
                "4. Mandar mensaje privado \n" +
                "5. Obtener historial de un chat\n" +
                "0. Salir");
        return msj;
    }

    public static void listAudioFiles() {
        File folder = new File("audios");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            System.out.println("Historial de audios:");
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("No se encontraron archivos de audio.");
        }
    }

    // Método para reproducir un archivo de audio a través de UDP
    public static void playAudioViaUDP(String audioFileName) {
        try {
            InetAddress IPAddress = InetAddress.getByName("127.0.0.1"); // Dirección IP del servidor

            PlayerSender sender = new PlayerSender("audios/" + audioFileName, IPAddress, UDP_PORT);
            sender.sendAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startServer() {
        try {
            AudioServer server = new AudioServer(9876); // Usa el puerto que prefieras
            server.start(); // Este método bloqueará y esperará conexiones
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playAudioFromServer() {
        try {
            AudioClient client = new AudioClient("127.0.0.1", 9876);
            client.play();
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
