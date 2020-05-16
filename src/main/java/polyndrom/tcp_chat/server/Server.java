package polyndrom.tcp_chat.server;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static final int USER_CONNECT_REQUEST = 0;
    public static final int USER_SEND_MESSAGE_REQUEST = 1;

    public static final int USER_CONNECTED_EVENT = 2;
    public static final int MESSAGE_RECEIVED_EVENT = 3;
    public static final int USER_DISCONNECTED_EVENT = 4;

    public static final int SERVER_SEND_PUBLIC_KEY = 5;
    public static final int CLIENT_SEND_PUBLIC_KEY = 6;
    public static final int SERVER_RECEIVE_PUBLIC_KEY = 7;
    public static final int CLIENT_RECEIVE_PUBLIC_KEY = 8;

    public static final String IP_ADDRESS = "0.0.0.0";
    public static final int PORT = 12555;

    public static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    private ServerSocket serverSocket;

    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    public static void generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(512);
            KeyPair pair = keyGen.generateKeyPair();
            publicKey = pair.getPublic();
            privateKey = pair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Server() throws IOException {
        generateKeys();
        serverSocket = new ServerSocket(PORT, 10, InetAddress.getByName(IP_ADDRESS));
        validateClients();
        while (true) {
            try {
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client);
                Thread thread = new Thread(clientHandler);
                thread.start();
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
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
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
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
