package polyndrom.tcp_chat.server;

import com.google.gson.annotations.SerializedName;
import com.sun.istack.internal.NotNull;

public class Request {

    enum Type {
        @SerializedName("0")
        CREATE_CHAT,
        @SerializedName("1")
        JOIN_CHAT,
        @SerializedName("2")
        SEND_MESSAGE
    }

    Type type;

     protected Request(@NotNull Type type) {
        this.type = type;
    }
}

class CreateChatRequest extends Request {

    public String chatName;
    public String ownerName;
    public String password;

    public CreateChatRequest(String chatName, String ownerName, String password) {
        super(Type.CREATE_CHAT);
        this.chatName = chatName;
        this.ownerName = ownerName;
        this.password = password;
    }
}

class JoinChatRequest extends Request {

    public String chatName;
    public String userName;
    public String password;

    public JoinChatRequest(@NotNull String chatName, @NotNull String userName, @NotNull String password) {
        super(Type.JOIN_CHAT);
        this.chatName = chatName;
        this.userName = userName;
        this.password = password;
    }
}

class SendMessageRequest extends Request {

    public String chatName;
    public String userName;
    public String message;

    public SendMessageRequest(@NotNull String chatName, @NotNull String userName, @NotNull String message) {
        super(Type.SEND_MESSAGE);
        this.chatName = chatName;
        this.userName = userName;
        this.message = message;
    }
}
