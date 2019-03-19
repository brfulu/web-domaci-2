package server;

import com.google.gson.Gson;
import common.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;

public class ServerThread implements Runnable {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Gson gson;
    private Game game;
    private Player player;

    public ServerThread(Socket socket, Game game) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.gson = new Gson();
        this.game = game;
    }

    @Override
    public void run() {
        try {
            var request = getRequest();
            player = new Player(request.getId());
            if (request.getIntent().equals(RequestIntent.REQUEST_CHAIR) && game.giveSeat(player)) {
                sendResponse(Status.OK, null);
                play();
            } else {
                sendResponse(Status.DENIED, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException | InterruptedException e) {
            return;
        }
    }

    private void play() throws BrokenBarrierException, InterruptedException, IOException {
        while (true) {
            if (game.getRounds() == 0) break;
            game.waitForFullTable();
            if (game.getRounds() == 0) break;

            if (game.currentPlayer().getId().equals(player.getId())) {
                sendMessage(MessageType.WAIT, "Waiting for bids");
                game.waitForBids();

                sendMessage(MessageType.DRAW, "Draw one of " + game.stickCount() + " sticks");
                var request = getRequest();
                int stickIndex = ((Double) request.getData()).intValue();

                StickType stick = game.draw(player, stickIndex);
                if (stick.equals(StickType.SHORT)) {
                    game.eject(player);
                    sendMessage(MessageType.EJECTED, "You lost");
                    break;
                }
            } else {
                sendMessage(MessageType.BID, "Make a bid");
                var request = getRequest();
                StickType stickType = StickType.valueOf((String) request.getData());
                game.bid(player, stickType);
            }
        }
    }

    private void sendResponse(Status status, Object data) throws IOException {
        Response response = new Response(status, data);
        output.writeUTF(gson.toJson(response));
    }

    private Request getRequest() throws IOException {
        String json = input.readUTF();
        return gson.fromJson(json, Request.class);
    }

    private void sendMessage(MessageType type, String body) throws IOException {
        Message message = new Message(type, body);
        output.writeUTF(gson.toJson(message));
    }
}
