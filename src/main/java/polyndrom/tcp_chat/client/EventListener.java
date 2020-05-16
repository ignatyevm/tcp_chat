package polyndrom.tcp_chat.client;

public interface EventListener {

    public void onUserConnected(String userName);
    public void onMessageReceived(String senderName, String message);
    public void onUserDisconnected(String userName);

}
