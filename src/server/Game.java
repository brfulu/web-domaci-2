package server;

import com.google.gson.Gson;
import common.ClientMessage;
import common.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private Gson gson;
    private AtomicInteger clientCount;
    private int rounds;
    private int tableSize;
    private Player[] players;
    private int playerIndex;

    public Game(int rounds, int tableSize) {
        this.gson = new Gson();
        this.rounds = rounds;
        this.tableSize = tableSize;
        this.clientCount = new AtomicInteger();
        this.players = new Player[tableSize];
        this.playerIndex = 0;
    }

    public Runnable createPlayer(Socket socket) {
        return new Player(socket);
    }

    class Player implements Runnable {
        private String uuid;
        private int points;
        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;

        public Player(Socket socket) {
            try {
                this.points = 0;
                this.uuid = UUID.randomUUID().toString();
                this.socket = socket;
                this.input = new DataInputStream(socket.getInputStream());
                this.output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            if (clientCount.incrementAndGet() > 6) {
                denyAccess();
            } else {
                play();
            }
        }

        private void play() {
            sendToClient("approved");
            findFreePlace();
            while (true) {
                if (players[playerIndex].uuid.equals(uuid)) {
                    // biram
                    sendToClient("choose");
                    var message = getFromClient();
                    System.out.println(message.getBody());
                } else {
                    // pogadjam
                    sendToClient("guess");
                    var message = getFromClient();
                    System.out.println(message.getBody());
                }

            }
        }

        private void findFreePlace() {
            synchronized (players) {
                for (int i = 0; i < tableSize; i++) {
                    if (players[i] == null) {
                        players[i] = this;
                        break;
                    }
                }
            }
        }

        private void denyAccess() {
            sendToClient("denied");
        }

        private void sendToClient(String body) {
            Message res = new Message(body);
            try {
                output.writeUTF(gson.toJson(res));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private ClientMessage getFromClient() {
            ClientMessage message = null;
            try {
                String json = input.readUTF();
                message = gson.fromJson(json, ClientMessage.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return message;
        }
    }
}
