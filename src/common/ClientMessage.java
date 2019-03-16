package common;

public class ClientMessage {
    private String uuid;
    private String body;

    public ClientMessage(String uuid, String body) {
        this.uuid = uuid;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getUuid() {
        return uuid;
    }
}
