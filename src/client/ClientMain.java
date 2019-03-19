package client;

public class ClientMain {
    public static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator(25);
        Thread thread = new Thread(orchestrator);
        thread.start();
    }
}
