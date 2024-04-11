package chat.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import main.Server;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    private Chatters clients;

    public ClientHandler(Socket clientSocket, Chatters clients) throws IOException {
        // asignar los objetos que llegan a su respectivo atributo en la clase
        this.clientSocket = clientSocket;
        this.clients = clients;
        initializeStreams();
    }

    // Establece canales de comunicación
    private void initializeStreams() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    private void authenticateClient() throws IOException {
        do {
            out.println("Introduce tu nombre de usuario: ");
            clientName = in.readLine();

            if (clients.alreadyExist(clientName)) {
                out.println("El nombre de usuario ya esta en uso");
            } else {
                out.println("Aceptado");
                break;
            }
        } while (true);
    }

    private void announceNewUser() {
        // Se notifica a los demás clientes que ha ingresado un nuevo usuario
        clients.broadcastMessage(clientName + " se ha unido al chat!");
        // Se agrega el nuevo usuario a la lista de clientes
        clients.addClient(new Person(clientName, out));
    }

    private void handleClientMessages() throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            if (message.startsWith("REQUEST_HISTORY")) {
                sendChatHistory();
            } else if (message.startsWith("PLAY_AUDIO:")) {
                String audioFileName = message.substring("PLAY_AUDIO:".length());
                out.println("PLAY_AUDIO_STARTED:" + audioFileName);
                sendAudioToClient(audioFileName);
            } else {
                processMessage(message);
            }
        }
    }

    private void sendChatHistory() {
        StringBuilder msj = new StringBuilder();
        for (String msg : Server.getChatHistory()) {
            msj.append(msg).append("\n");
        }
        out.println(msj);
    }

    private void sendAudioToClient(String audioFileName) {
        try {
            // Abrir el archivo de audio
            File audioFile = new File("audios/" + audioFileName);
            FileInputStream fileInputStream = new FileInputStream(audioFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // Crear un socket UDP
            DatagramSocket socket = new DatagramSocket();

            // Leer el archivo de audio y enviar los datos en paquetes UDP
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                DatagramPacket packet = new DatagramPacket(buffer, bytesRead, clientSocket.getInetAddress(),
                        clientSocket.getPort());
                socket.send(packet);
            }

            // Cerrar recursos
            bufferedInputStream.close();
            socket.close();
        } catch (IOException e) {
            // Manejo de excepciones
        }
    }

    private void processMessage(String message) {
        if (message.startsWith("/msg")) {
            processPrivateMessage(message);
        } else if (message.startsWith("/creategroup")) {
            createGroup(message);
        } else if (message.startsWith("/joingroup")) {
            joinGroup(message);
        } else if (message.startsWith("/groupmsg")) {
            sendGroupMessage(message);
        } else {
            Server.addToChatHistory(clientName + ": " + message);
            clients.broadcastMessage(clientName + ": " + message);
        }
    }

    private void processPrivateMessage(String message) {
        String[] parts = message.split(" ", 3);
        String receiver = parts[1];
        String privateMessage = parts[2];
        clients.sendPrivateMessage(receiver, clientName, privateMessage);
    }

    private void createGroup(String message) {
        String[] parts = message.split(" ", 2);
        String groupName = parts[1];
        clients.addGroup(new Group(groupName));
        clients.joinGroup(groupName, clientName);
    }

    private void joinGroup(String message) {
        String[] parts = message.split(" ", 2);
        String groupName = parts[1];
        clients.joinGroup(groupName, clientName);
    }

    private void sendGroupMessage(String message) {
        String[] parts = message.split(" ", 2);
        String[] groupAndMessage = parts[1].split(" ", 2);
        String groupName = groupAndMessage[0];
        String groupMessage = groupAndMessage[1];
        clients.sendGroupMessage(groupName, clientName, groupMessage);
    }

    @Override
    public void run() {
        try {
            authenticateClient();
            announceNewUser();
            handleClientMessages();
        } catch (IOException e) {
            System.err.println("Error handling client messages: " + e.getMessage());
        } finally {
            cleanUp();
        }
    }

    private void cleanUp() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
            clients.broadcastMessage(clientName + " se ha desconectado del chat");
            clients.removeClient(clientName);
        } catch (IOException ex) {
            System.err.println("Error cleaning up client connection: " + ex.getMessage());
        }
    }
}