import Client.ClientThread;
import Client_coppy.ClientThreadCoppy;

import java.io.IOException;
import java.net.Socket;

public class ClientCoppy {
    private static Socket clientSocket; //сокет для общения

    public static void main(String[] args) throws IOException {
        try {
            clientSocket = new Socket("localhost", 8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ClientThreadCoppy(clientSocket);
    }
}
