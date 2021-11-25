package Server;

import Help.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

    public static final int PORT = 8080;
    public static LinkedList<User> serverList = new LinkedList<>();

    public Server() throws IOException {
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server up");
        try {
            while (true) {
                Socket socket = server.accept(); // Блокируется до возникновения нового соединения:
                serverList.add(new User(new ServerThread(socket) , null , null)); // добавить новое соединенние в список
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.close();
        }
    }


}
