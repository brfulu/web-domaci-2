package client;

import common.Helper;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Orchestrator implements Runnable {
    private int clientCount;
    private ScheduledExecutorService executorService;

    public Orchestrator(int clientCount) {
        this.clientCount = clientCount;
        this.executorService = Executors.newScheduledThreadPool(30);
    }

    public void run() {
        try {
            for (int i = 0; i < this.clientCount; i++) {
                Thread.sleep(100);
                this.executorService.schedule(new Client(), Helper.randomInt(0, 1000), TimeUnit.MILLISECONDS);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.executorService.shutdown();
    }
}
