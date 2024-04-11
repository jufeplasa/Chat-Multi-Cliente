package chat.server;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Chatters {
    // tendra una lista de personas que seran nuestros clientes
    // cada persona tiene un nombre y un canal para enviarle mensajes
    private Set<Person> clients;
    private Set<Group> groups;

    public Chatters() {
        this.clients = new HashSet<Person>();
        this.groups = new HashSet<Group>();
    }

    // metodo para verificar si un usuario existe, retorna true si existe
    public synchronized boolean alreadyExist(String userName) {
        for (Person cliente : clients) {
            if (cliente.getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendPrivateMessage(String receptor, String emisor, String message) {
        for (Person cliente : clients) {
            if (cliente.getName().equals(receptor)) {
                cliente.getOut().println("(" + emisor + ") : " + message);
                break;
            }
        }
    }

    // metodo para agregar un usuario nuevo
    public synchronized void addClient(Person cliente) {
        clients.add(cliente);
    }

    // metodo para eliminar un usuario
    public synchronized void removeClient(String clientName) {
        // Buscar al cliente por su nombre y eliminarlo de la lista
        Iterator<Person> iterator = clients.iterator();
        while (iterator.hasNext()) {
            Person cliente = iterator.next();
            if (cliente.getName().equals(clientName)) {
                iterator.remove(); // Eliminar al cliente de la lista
                break;
            }
        }
    }

    // metodo para agregar un grupo
    public synchronized void addGroup(Group group) {
        groups.add(group);
    }

    // metodo para unirse a un grupo
    public synchronized int joinGroup(String groupName, String clientName) {
        int response = -1;
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                group.addMember(clientName);
                response = 0;
            }
        }
        return response;
    }

    // metodo para mandar un mensaje a un grupo
    public synchronized String sendGroupMessage(String groupName, String clientName, String message) throws Exception {
        String msj = "";
        boolean isFound = false;
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                isFound = true;
                for (String member : group.getMembers()) {
                    for (Person client : clients) {
                        if (client.getName().equals(member)) {
                            msj = "(" + groupName + ") (" + clientName + ") : " + message;
                            group.addHistory("(" + groupName + ") (" + clientName + ") : " + message);

                        }
                    }
                }
            }
        }
        if (!isFound) {
            throw new Exception("Groupo '" + groupName + "' no fue encontrado.");
        }
        return msj;
    }

    // metodo para enviar un mensaje a todos los usuarios
    public synchronized void broadcastMessage(String message) {
        for (Person client : clients) {
            client.getOut().println(message);
            client.getOut().println();// kimpiamos
        }
    }

    // Método para obtener el historial de un chat particular
    public synchronized String getHistory(String groupName, String clientName) throws Exception {
        String msj = "";
        boolean isFound = false;
        for (Group group : groups) {
            if (group.getName().equals(groupName)) { // Encontramos el grupo
                isFound = true;
                for (String member : group.getMembers()) { // Buscamos el mienbro
                    for (Person client : clients) { // Seguimos buscando en los clientes
                        if (client.getName().equals(member)) { // Lo encontramos
                            // Entonces podemos extraer el historial
                            msj += "- " + group.getName() + " " + group.getHistory();
                        }
                    }
                }
            }
            if (!isFound) {
                throw new Exception("Groupo '" + groupName + "' no fue encontrado.");
            }
        }
        return msj;
    }
}