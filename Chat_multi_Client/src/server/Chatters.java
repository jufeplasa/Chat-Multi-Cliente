package server;
import java.util.Set;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Chatters{
   //tendra una lista de personas que seran nuestros clientes
   //cada persona tiene un nombre y un canal para enviarle mensajes

    private Set<Person> clientes;
     
    public Chatters(){
    	this.clientes = new HashSet<Person>();
    }

    //metodo para verificar si un usuario existe, retorna true si existe
    public synchronized boolean alreadyExist(String userName){
    	for(Person cliente : clientes){
    		if(cliente.getName().equals(userName)) {
    			return true;
    		}
    	}
    	return false;
    }

    //metodo para agregar un usuario nuevo

    public synchronized void addClient(Person cliente){
    	clientes.add(cliente);
    }

    //metodo para eliminar un usuario

    public synchronized void removeClient(Person cliente){
    	clientes.remove(cliente);
    }

    //metodo para enviar un mensaje a todos los usuarios

    public synchronized void broadcastMessage(String message){
    	for(Person cliente : clientes){
    		cliente.getOut().println(message);
    	}
    }  

}