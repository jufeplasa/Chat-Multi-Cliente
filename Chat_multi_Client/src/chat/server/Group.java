package chat.server;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<String> members;

    public Group(String name) {
        this.name = name;
        this.members = new ArrayList<>();
    }

    // Añadir un miembro al grupo
    public void addMember(String member) {
        if (!isMember(member)) {
            members.add(member);
        }
    }

    // Quitar un miembro del grupo
    public boolean removeMember(String member) {
        return members.remove(member);
    }

    // Comprobar si un usuario es miembro del grupo
    public boolean isMember(String member) {
        return members.contains(member);
    }

    // Getter para el nombre del grupo
    public String getName() {
        return this.name;
    }

    // Getter para la lista de miembros
    public List<String> getMembers() {
        return new ArrayList<>(members); // Devuelve una copia para evitar la modificación externa
    }
}
