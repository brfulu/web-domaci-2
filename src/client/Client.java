package client;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;

    public Client(String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Client client = new Client("localhost", 2019);
        }
    }
}
