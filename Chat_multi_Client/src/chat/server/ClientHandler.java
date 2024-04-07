package chat.server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    Chatters clientes;

    public ClientHandler(Socket clientSocket, Chatters clientes) throws IOException {
        // asignar los objetos que llegan a su respectivo atributo en la clase
        this.clientSocket = clientSocket;
        this.clientes = clientes;
        // Establece canales de comunicación
        out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            boolean isvalidName;

            do {
                // Solicitar el nombre de usuario
                out.println("Introduce tu nombre de usuario: ");
                clientName = in.readLine();
                System.out.println(clientName);

                // Verificar si el nombre de usuario ya está en uso
                if (clientes.alreadyExist(clientName)) {
                    out.println("El nombre de usuario ya esta en uso");
                    isvalidName = false;
                } else {
                    out.println("Aceptado");
                    isvalidName = true;
                }

            } while (!isvalidName);

            // Se notifica a los demás clientes que ha ingresado un nuevo usuario
            clientes.broadcastMessage(clientName + " se ha unido al chat!");

            // Se agrega el nuevo usuario a la lista de clientes
            Person p = new Person(clientName, out);
            clientes.addClient(p);

            // Mensajes del cliente
            String message;
            while ((message = in.readLine()) != null) {

                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    String recipient = parts[0].trim();
                    String content = parts[1].trim();
                    clientes.sendPrivateMessage(recipient, clientName, content);
                } else {
                    clientes.broadcastMessage(clientName + ": " + message);

                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                clientes.broadcastMessage(clientName + " se ha desconectado del chat");
                clientes.removeClient(clientName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public String extractName(String message, int option) {
        String info = "";
        if (option == 1) {
            String temp = message.split(" ")[0];
            info = temp.split("-")[1];
        } else if (option == 2) {
            info = message.split(" ", 2)[1];
        }

        return info;
    }
}