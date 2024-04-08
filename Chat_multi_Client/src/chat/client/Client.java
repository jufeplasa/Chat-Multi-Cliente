package chat.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import chat.server.Chatters;
import chat.server.ClientHandler;

public class Client extends Thread {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6789;
    static Scanner sc = new Scanner(System.in);
    static ClientHandler clientHandler;
    private static Chatters clientes;

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            // Definicion socket del cliente
            Socket clientSocket = new Socket(SERVER_IP, PORT);
            System.out.println("Conectado al servidor.");
            // Lector
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Lector del socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Escritor
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

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
            Lector lector = new Lector(in);

            // inicar el hilo
            lector.start();
            String mensajeCliente;

            do {
                // estar atento a la entrada del usuario para poner los mensajes en el canal de
                // salida out
                mensajeCliente = userInput.readLine();
                // Aquí valida si ingresa la palabra clave menú
                if (mensajeCliente.toUpperCase().equals("MENU") || mensajeCliente.toUpperCase().equals("MENÚ")) {
                    System.out.println(showMenu());
                    int option = sc.nextInt();

                    switch (option) {
                        case 1:
                            out.println("REQUEST_HISTORY"); // Solicitud al servidor para el historial de mensajes
                            System.out.println("Historial de mensajes:");
                            String historyResponse = in.readLine(); // Espera la respuesta del servidor
                            System.out.println(historyResponse); // Muestra el historial de mensajes en la consola del
                                                                 // cliente
                            break;
                        case 2:

                            break;
                        case 3:

                            break;
                        case 4:

                            break;
                        case 5:

                            break;
                        case 0:
                            System.exit(0);
                            break;

                        default:
                            break;
                    }

                }
                out.println(mensajeCliente);
            } while (mensajeCliente != "salir");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String showMenu() {
        String msj = "";
        msj = ("---------Menu desde chat------------- \n" +
                "1. Ver historial de mensajes \n " +
                "2. Ver historial de audios \n" +
                "3. Reproducir un audio \n" +
                "4. Iniciar/entrar a una llamada \n" +
                "0. Seguir chateando");
        return msj;
    }

}
