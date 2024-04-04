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

    public ClientHandler(Socket clientSocket,Chatters clientes) throws IOException {
        //asignar los objetos que llegan a su respectivo atributo en la clase
        this.clientSocket = clientSocket;
        this.clientes = clientes;
        // Establece canales de comunicación
        out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        in= new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
        	boolean isvalidName;
        	do {
        		//Solicitar el nombre de usuario
                out.println("Introduce tu nombre de usuario: ");
                clientName = in.readLine();
                System.out.println(clientName);
                
                
                //Verificar si el nombre de usuario ya está en uso
                if(clientes.alreadyExist(clientName)){
                    out.println("El nombre de usuario ya esta en uso");
                    isvalidName=false;
                }
                else {
                	out.println("Aceptado");
                	isvalidName=true;
                }

        	}while(!isvalidName);
           

            
        

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

        } catch (Error | IOException e){
            e.printStackTrace();
        }

    }
}