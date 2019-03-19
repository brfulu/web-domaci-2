package common;

public class Request {
    private String id;
    private RequestIntent intent;
    private Object data;

    public Request(String id, RequestIntent intent, Object data) {
        this.id = id;
        this.intent = intent;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public RequestIntent getIntent() {
        return intent;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Request: [" + id + ", " + intent + ", " + data + "]";
    }
}
