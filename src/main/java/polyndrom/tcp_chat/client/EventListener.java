package polyndrom.tcp_chat.client;

public interface EventListener {

    public void onUserConnected(String userName);
    public void onMessageReceive(String senderName, String message);

}
