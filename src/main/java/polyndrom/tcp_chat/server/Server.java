package polyndrom.tcp_chat.server;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static final int USER_CONNECT_REQUEST = 0;
    public static final int USER_SEND_MESSAGE_REQUEST = 1;

    public static final int NEW_USER_EVENT = 2;
    public static final int NEW_MESSAGE_EVENT = 3;

    public static final String IP_ADDRESS = "0.0.0.0";
    public static final int PORT = 12555;

    public static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    private ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT, 10, InetAddress.getByName(IP_ADDRESS));
        while (true) {
            try {
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client);
                Thread thread = new Thread(clientHandler);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
