package polyndrom.tcp_chat.client;

import polyndrom.tcp_chat.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private String userName;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private EventListener eventListener;

    public Client(String userName) throws IOException {
        this.userName = userName;
        socket = new Socket(InetAddress.getByName(Server.IP_ADDRESS), Server.PORT);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    if (input.available() <= 0) {
                        continue;
                    }
                    int eventType = input.readInt();
                    if (eventType == -1) continue;;
                    System.out.println("Current user: " + userName);
                    switch (eventType) {
                        case Server.USER_CONNECTED_EVENT: {
                            String newUserName = input.readUTF();
                            System.out.println("Connected user: " + newUserName);
                            eventListener.onUserConnected(newUserName);
                        }
                        break;
                        case Server.MESSAGE_RECEIVED_EVENT: {
                            String senderName = input.readUTF();
                            String message = input.readUTF();
                            System.out.println(senderName + ": " + message);
                            eventListener.onMessageReceived(senderName, message);
                        }
                        break;
                        case Server.USER_DISCONNECTED_EVENT: {
                            String newUserName = input.readUTF();
                            System.out.println("Disconnected user: " + newUserName);
                            eventListener.onUserDisconnected(newUserName);
                        }
                        break;
                    }
                    System.out.println("=========================");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void connect() throws IOException {
        output.writeInt(Server.USER_CONNECT_REQUEST);
        output.writeUTF(userName);
    }

    public void sendMessage(String message) throws IOException {
        output.writeInt(Server.USER_SEND_MESSAGE_REQUEST);
        output.writeUTF(userName);
        output.writeUTF(message);
    }

    public void disconnect() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    public boolean isConnected() {
        return !socket.isClosed();
    }

    public void registerEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public String getUserName() {
        return userName;
    }

    public static void main(String[] args) throws IOException {
        Client client1 = new Client("user1");
        client1.registerEventListener(new EventListener() {
            @Override
            public void onUserConnected(String userName) {

            }

            @Override
            public void onMessageReceived(String senderName, String message) {

            }

            @Override
            public void onUserDisconnected(String userName) {

            }
        });
        client1.connect();
        client1.sendMessage("hello world");
    }

}