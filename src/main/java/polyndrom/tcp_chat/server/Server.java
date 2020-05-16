package polyndrom.tcp_chat.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static final int USER_CONNECT_REQUEST = 0;
    public static final int USER_SEND_MESSAGE_REQUEST = 1;

    public static final int USER_CONNECTED_EVENT = 2;
    public static final int MESSAGE_RECEIVED_EVENT = 3;
    public static final int USER_DISCONNECTED_EVENT = 4;

    public static final String IP_ADDRESS = "0.0.0.0";
    public static final int PORT = 12555;

    public static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    private ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT, 10, InetAddress.getByName(IP_ADDRESS));
        validateClients();
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

    void validateClients() {
        new Thread(() -> {
            while (true) {
                Set<ClientHandler> disconnectedClients = new HashSet<>();
                try {
                    for (ClientHandler clientHandler : clients.values()) {
                        try {
                            clientHandler.getOutput().writeInt(-1);
                        } catch (SocketException e) {
                            clientHandler.close();
                            clients.remove(clientHandler.getUserName());
                            disconnectedClients.add(clientHandler);
                            System.out.println("Disconnected: " + clientHandler.getUserName());
                        }
                    }
                    for (ClientHandler clientHandler : clients.values()) {
                        for (ClientHandler disconnectedClient : disconnectedClients) {
                            clientHandler.getOutput().writeInt(Server.USER_DISCONNECTED_EVENT);
                            clientHandler.getOutput().writeUTF(disconnectedClient.getUserName());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
