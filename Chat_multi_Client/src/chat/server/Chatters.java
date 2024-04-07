package chat.server;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class Chatters {
    // tendra una lista de personas que seran nuestros clientes
    // cada persona tiene un nombre y un canal para enviarle mensajes

    private Set<Person> clientes;

    public Chatters() {
        this.clientes = new HashSet<Person>();
    }

    // metodo para verificar si un usuario existe, retorna true si existe
    public synchronized boolean alreadyExist(String userName) {
        for (Person cliente : clientes) {
            if (cliente.getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendPrivateMessage(String receptor, String emisor, String message) {
        for (Person cliente : clientes) {
            if (cliente.getName().equals(receptor)) {
                cliente.getOut().println("(" + emisor + ") : " + message);
                break;
            }
        }
    }

    // metodo para agregar un usuario nuevo
    public synchronized void addClient(Person cliente) {
        clientes.add(cliente);
    }

    // metodo para eliminar un usuario

    public synchronized void removeClient(String clientName) {
        // Buscar al cliente por su nombre y eliminarlo de la lista
        Iterator<Person> iterator = clientes.iterator();
        while (iterator.hasNext()) {
            Person cliente = iterator.next();
            if (cliente.getName().equals(clientName)) {
                iterator.remove(); // Eliminar al cliente de la lista
                break;
            }
        }
    }

    // metodo para enviar un mensaje a todos los usuarios
    public synchronized void broadcastMessage(String message) {
        for (Person cliente : clientes) {
            cliente.getOut().println(message);
        }
    }

}