package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;

public class Server {

    // Interfaz de cliente

     public static void main(String[] args) {

        int PORT = 6789;
        
        Chatters clientes = new Chatters(); //lista de clientes

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado. Esperando clientes...");

            while (true) {
                // Se espera a que se conecte un cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket);
                
                //crea el objeto para gestionar al cliente y le envia la informacion necesaria
                //inicia el hilo para ese cliente}

                ClientHandler clientHandler = new ClientHandler(clientSocket, clientes);
                Thread t = new Thread(clientHandler);
                t.start();
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
}
