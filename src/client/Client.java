package client;

import com.google.gson.Gson;
import common.ClientMessage;
import common.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable {
    private Gson gson;
    private String host;
    private int port;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public Client(String host, int port) {
        gson = new Gson();
        this.host = host;
        this.port = port;
    }

    private void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        while (true) {
            Message message = getFromServer();
            if (message.getBody().equals("choose")) {
                sendToServer("3");
            } else if (message.getBody().equals("guess")) {
                sendToServer("normal");
            }
            break;
        }
    }

    private void sendToServer(String body) {
        Message message = new Message(body);
        try {
            String json = gson.toJson(message);
            output.writeUTF(json);
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message getFromServer() {
        Message message = null;
        try {
            String json = input.readUTF();
            System.out.println(json);
            message = gson.fromJson(json, Message.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public void run() {
        int attempts = 0;
        while (attempts < 5) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connect(host, port);
            Message message = getFromServer();
            if (message.getBody().equals("approved")) {
                play();
                break;
            }
            disconnect();
            attempts++;
        }
    }

    public static void main(String[] args) {
        int clientCount = 7;
        ExecutorService pool = Executors.newFixedThreadPool(20);
        for (int i = 0; i < clientCount; i++) {
            pool.execute(new Client("localhost", 2019));
        }
    }
}
