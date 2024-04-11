package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chat.server.Chatters;
import chat.server.ClientHandler;

public class Server {
    private static final int PORT = 6789;
    private static Chatters clients = new Chatters();
    private static Scanner scanner = new Scanner(System.in);
    private static ArrayList<String> chatHistory = new ArrayList<>();

    public static void main(String[] args) {
        startClientConnectionHandler();
        displayMenu();
    }

    // Método para iniciar el manejador de conexiones de clientes
    private static void startClientConnectionHandler() {
        // Crear un hilo para iniciar el servidor
        Thread serverThread = new Thread(Server::initiateServer);
        serverThread.start();
    }

    // Método para iniciar el servidor
    private static void initiateServer() {
        // Crear un socket del servidor
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado. Esperando clientes...");
            while (true) {
                // Esperar a que un cliente se conecte
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket);
                // Manejar la conexión del cliente
                handleClientConnection(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Error iniciando el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para manejar la conexión de un cliente
    private static void handleClientConnection(Socket clientSocket) throws IOException {
        // Crear un nuevo hilo para manejar la conexión del cliente
        ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
        new Thread(clientHandler).start();
    }

    // Método para mostrar el menú
    private static void displayMenu() {
        while (true) {
            System.out.println("\nMenú:");
            System.out.println("1. Agregar cliente");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    openCMD();
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void openCMD() {
        // Crear un proceso para ejecutar el comando para abrir CMD
        ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "cmd.exe", "/k",
                "java -cp Chat_multi_Client/bin chat.client.Client");
        builder.redirectErrorStream(true);

        try {
             // Ejecutar el proceso
            Process process = builder.start();
             // Esperar a que el proceso termine
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error ejecutando comando CMD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para agregar un mensaje al historial de chat
    public static void addToChatHistory(String message) {
        chatHistory.add(message);
    }

    // Método para obtener el historial de chat
    public static List<String> getChatHistory() {
        return new ArrayList<>(chatHistory);
    }
}
