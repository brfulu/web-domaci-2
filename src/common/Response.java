package common;

public class Response {
    private Status status;
    private Object data;

    public Response(Status status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }
}
