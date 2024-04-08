package main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import chat.server.*;
import chat.client.*;
import java.util.Scanner;

public class Server {
    private static Chatters clientes;
    private static ArrayList<String> chatHistory = new ArrayList<>();

    public static void main(String[] args) {
        clientes = new Chatters();

        // Iniciar el hilo para manejar conexiones de clientes
        Thread serverThread = new Thread(() -> {
            iniciarServidor();
        });
        serverThread.start();
        // Mostrar el menú y manejar los comandos del usuario en el hilo principal
        mostrarMenu();
    }

    public static void iniciarServidor() {
        int PORT = 6789;

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado. Esperando clientes...");

            while (true) {
                // Se espera a que se conecte un cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket);

                // crea el objeto para gestionar al cliente y le envia la informacion necesaria
                // inicia el hilo para ese cliente}
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientes);
                Thread t = new Thread(clientHandler);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Menú:");
            System.out.println("1. Agregar cliente");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: \n");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    abrirCMD();
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    public static void abrirCMD() {
        try {
            // Crear un proceso para ejecutar el comando para abrir CMD
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "cmd.exe", "/c",
                    "java -cp Chat_multi_Client/bin chat.client.Client");
            builder.redirectErrorStream(true);

            // Ejecutar el proceso
            Process process = builder.start();

            // Esperar a que el proceso termine
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método para agregar un mensaje al historial de chat
    public static void addToChatHistory(String message) {
        chatHistory.add(message);
    }

    // Método para obtener el historial de chat
    public static ArrayList<String> getChatHistory() {
        return chatHistory;
    }
}
