package polyndrom.tcp_chat.server;

import com.google.gson.annotations.SerializedName;
import com.sun.istack.internal.NotNull;

public class Response {

    enum Status {
        @SerializedName("0")
        OK,
        @SerializedName("1")
        ERROR
    }

    Status status;

    protected Response(Status status) {
        this.status = status;
    }

}

class OKResponse extends Response {

    public OKResponse() {
        super(Status.OK);
    }

}

class ErrorResponse extends Response {

    String message;

    public ErrorResponse(@NotNull String message) {
        super(Status.ERROR);
        this.message = message;
    }

}

class Event extends OKResponse {

    enum Type {
        @SerializedName("0")
        USER_JOIN,
        @SerializedName("1")
        NEW_MESSAGE
    }

    Type type;

    protected Event(Type type) {
        this.type = type;
    }

}


class UserJoinEvent extends Event {

    String userName;

    public UserJoinEvent(@NotNull String userName) {
        super(Type.USER_JOIN);
        this.userName = userName;
    }
}

class NewMessageEvent extends Event {

    String userName;
    String message;

    public NewMessageEvent(@NotNull String userName, @NotNull String message) {
        super(Type.NEW_MESSAGE);
        this.userName = userName;
        this.message = message;
    }
}
