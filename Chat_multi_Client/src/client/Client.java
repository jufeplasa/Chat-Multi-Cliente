package client;



import java.io.*;
import java.net.*;

public class Client extends Thread{
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6789;

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            //Definicion socket del cliente
            Socket clientSocket = new Socket(SERVER_IP, PORT);
            System.out.println("Conectado al servidor.");
            //Lector
            BufferedReader userInput = new BufferedReader(new 
            InputStreamReader(System.in));

            //Lector del socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //Escritor
            PrintWriter out= new PrintWriter(clientSocket.getOutputStream(), true);

            String name;
            String mensajeServer;
            
            do{
                //solicitar al usuario un alias, o nombre
            	System.out.println("espero mensaje");
                mensajeServer=in.readLine();
                System.out.println("recibo el mensaje");
                System.out.print(mensajeServer);

                // Escribir y enviarlo al   servidor
                name=userInput.readLine();
                out.println(name);

                //Respuesta del cliente. 
                mensajeServer=in.readLine();
                System.out.println(mensajeServer);
                //no debe salir de este bloque hasta que el nombre no sea aceptado
            } while(mensajeServer=="Aceptado");
            
            
            
            //creamos el objeto Lector e iniciamos el hilo que nos permitira estar atentos a los mensajes
            //que llegan del servidor
            Lector lector = new Lector(in);

            //inicar el hilo
            lector.start();
            String mensajeCliente;

            do{
                //estar atento a la entrada del usuario para poner los mensajes en el canal de salida out
                mensajeCliente=userInput.readLine();
                out.println(mensajeCliente);
            } while(mensajeCliente!="salir");
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
