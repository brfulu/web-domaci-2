package server;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private AtomicInteger clientCount;
    private int roundCount;
    private int tableSize;
    private Player[] activePlayers;
    private int currentIndex;

    public Game(int roundCount, int tableSize) {
        this.roundCount = roundCount;
        this.tableSize = tableSize;
        this.clientCount = new AtomicInteger();
        this.activePlayers = new Player[tableSize];
        this.currentIndex = 0;
    }

    public Runnable createPlayer(Socket socket) {
        return new Player(socket);
    }

    class Player implements Runnable {
        private String uuid;
        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;

        public Player(Socket socket) {
            try {
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
            System.out.println("Dosao igrac: " + uuid);
            if (clientCount.incrementAndGet() > 6) {
                denyAccess();
            } else {
                play();
            }
        }

        private void play() {
            findFreePlace();
            while (true) {

            }
        }

        private void findFreePlace() {
            synchronized (activePlayers) {
                for (int i = 0; i < tableSize; i++) {
                    if (activePlayers[i] == null) {
                        activePlayers[i] = this;
                        break;
                    }
                }
            }
        }

        private void denyAccess() {
            System.out.println("Probao igrac: " + uuid);
        }
    }
}
