package server;


public class ServerMain {
    public static void main(String[] args) {
        Thread server = new Thread(new Server(2019, 30));
        server.start();
    }
}
