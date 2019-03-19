package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class Server implements Runnable {
    private ServerSocket listener;
    private int poolSize;
    private ExecutorService pool;

    public Server(int port, int poolSize) {
        try {
            this.poolSize = poolSize;
            listener = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
                pool = Executors.newFixedThreadPool(poolSize);
                Game game = new Game(12, 6);

                Thread resultThread = new Thread(() -> {
                    try {
                        game.getGameLatch().await();
                        pool.shutdownNow();
                        game.printResult();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                resultThread.start();

                while (true) {
                    var serverThread = new ServerThread(listener.accept(), game);
                    pool.execute(serverThread);
                }
            } catch (IOException | RejectedExecutionException | InterruptedException e) {
                continue;
            }
        }
    }
}
