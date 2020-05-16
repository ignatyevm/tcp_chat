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
            while (true) {
                try {
                    if (input.available() <= 0) {
                        continue;
                    }
                    int eventType = input.readInt();
                    System.out.println("Current client: " + userName);
                    switch (eventType) {
                        case Server.NEW_USER_EVENT: {
                            String newUserName = input.readUTF();
                            System.out.println("New user: " + newUserName);
                            eventListener.onUserConnected(newUserName);
                        }
                        break;
                        case Server.NEW_MESSAGE_EVENT: {
                            String senderName = input.readUTF();
                            String message = input.readUTF();
                            System.out.println(senderName + ": " + message);
                            eventListener.onMessageReceive(senderName, message);
                        }
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

    public void registerEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public static void main(String[] args) throws IOException {
        Client client1 = new Client("user1");
        client1.registerEventListener(new EventListener() {
            @Override
            public void onUserConnected(String userName) {

            }

            @Override
            public void onMessageReceive(String senderName, String message) {

            }
        });
        client1.connect();
        client1.sendMessage("hello world");
    }

}