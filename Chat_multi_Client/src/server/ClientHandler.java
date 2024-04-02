package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    Chatters clientes;

    public ClientHandler(Socket clientSocket,Chatters clientes) {
        //asignar los objetos que llegan a su respectivo atributo en la clase
        this.clientSocket = clientSocket;
        this.clientes = clientes;
        
    }

    @Override
    public void run() {
        try {
            // Establece canales de comunicación
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            //Solicitar el nombre de usuario
            System.out.print("Introduce tu nombre de usuario: ");
            clientName = in.readLine();

            //Verificar si el nombre de usuario ya está en uso
            if(clientes.alreadyExist(clientName)){
                System.out.println("El nombre de usuario ya está en uso");
                return;
            }


            // Se notifica a los demás clientes que ha ingresado un nuevo usuario
            clientes.broadcastMessage(clientName + " se ha unido al chat!");

            // Se agrega el nuevo usuario a la lista de clientes
            Person p = new Person(clientName, out);
            clientes.addClient(p);


            //Mensajes del cliente
            String message;
            while ((message = in.readLine())!=null){
                clientes.broadcastMessage(clientName+": "+message);
            }

            //Elimina al cliente cuando se desconecta
            clientes.removeClient(p);
            clientSocket.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}