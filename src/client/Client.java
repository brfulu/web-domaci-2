package client;

import com.google.gson.Gson;
import common.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [Client] REQUEST_CHAIR id
 * [Server] OK/DENIED
 *
 * [Server] WAIT Waiting for other players to bid
 * [Server] DRAW Draw a stick
 * [Client] DRAW stick_id
 * [Server] OK/DENIED
 *
 * [Server] WAIT Waiting for players to arrive
 * [Server] BID Place a bid normal/short
 * [Client] BID normal/short
 * [Server] OK/DENIED
 */

public class Client implements Runnable {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Gson gson;

    private String id;
    public static AtomicInteger counter = new AtomicInteger();

    public Client() throws IOException {
        this.socket = new Socket("localhost", 2019);
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.gson = new Gson();
//        this.id = UUID.randomUUID().toString();
        this.id = Integer.toString(counter.incrementAndGet());
    }

    private void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        sendRequest(RequestIntent.REQUEST_CHAIR, null);
        var response = getResponse();
        if (response.getStatus().equals(Status.OK)) {
            play();
        }
        disconnect();
    }

    public void play() {
        while (true) {
            Message message = getMessage();
            MessageType type = message.getType();
            if (type.equals(MessageType.WAIT)) {
              //  System.out.println(id + " " + message.getBody());
            } else if (type.equals(MessageType.BID)) {
                // BID
                StickType stickType = Helper.randomBoolean() ? StickType.NORMAL : StickType.SHORT;
                System.out.println(id + " BID " + stickType);
                sendRequest(RequestIntent.BID, stickType);
            } else if (type.equals(MessageType.DRAW)) {
                // DRAW
                System.out.println(message.getBody());
                int stickCount = Integer.parseInt(message.getBody().replaceAll("[^0-9]", ""));
                int stickIndex = Helper.randomInt(1, stickCount);
                System.out.println(id + " DRAW " + stickIndex);
                sendRequest(RequestIntent.DRAW, stickIndex);
                System.out.println();
            } else if (type.equals(MessageType.EJECTED)) {
                // EJECTED
                System.out.println(id + " EJECTED");
                System.out.println();
                break;
            } else {
                // ERROR
                System.out.println(id + " Error message!" + " " + type);
            }
        }
    }

    private void sendRequest(RequestIntent intent, Object data) {
        Request request = new Request(this.id, intent, data);
        try {
            String json = gson.toJson(request);
            output.writeUTF(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Response getResponse() {
        try {
            String json = input.readUTF();
            return gson.fromJson(json, Response.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Message getMessage() {
        try {
            String json = input.readUTF();
            return gson.fromJson(json, Message.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
