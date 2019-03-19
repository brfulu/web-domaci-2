package common;

public class Message {
    private MessageType type;
    private String body;

    public Message(MessageType type, String body) {
        this.type = type;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public MessageType getType() {
        return type;
    }
}
