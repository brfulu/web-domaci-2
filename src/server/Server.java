package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//        [Server]Nema mesta
//        [Server]Daj prognozu
//        [Server]Pogodak/Promasaj
//        [Server]Ispao si/Nisi ispao
//        [Server]Pobedio si
//        [Server]Izvuci stapic
//
//        [Client]Jeste kraci/Nije kraci
//        [Client]Broj stapica

public class Server {
    private ServerSocket listener;
    private ExecutorService pool;

    public Server(int port, int poolSize) {
        try {
            listener = new ServerSocket(port);
            pool = Executors.newFixedThreadPool(poolSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        Game game = new Game(3, 6);
        while (true) {
            pool.execute(game.createPlayer(listener.accept()));
        }
    }

    public static void main(String[] args) {
        var server = new Server(2019, 20);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
