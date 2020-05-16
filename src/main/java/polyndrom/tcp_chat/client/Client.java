package polyndrom.tcp_chat.client;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import polyndrom.tcp_chat.server.SecuredDataInputStream;
import polyndrom.tcp_chat.server.SecuredDataOutputStream;
import polyndrom.tcp_chat.server.Server;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Client {

    private String userName;
    private Socket socket;
    private SecuredDataInputStream input;
    private SecuredDataOutputStream output;
    private EventListener eventListener;

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private PublicKey serverPublicKey;

    public void generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        KeyPair pair = keyGen.generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
    }

    public void receivePublicKeyFromServer(DataInputStream dis, DataOutputStream dos) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        while (true) {
            if (dis.available() > 0) {
                int requestId = dis.readInt();
                if (requestId == Server.SERVER_SEND_PUBLIC_KEY) {
                    String key = dis.readUTF();
                    System.out.println("[Client] Server key: " + key);
                    byte[] byteKey = Base64.getDecoder().decode(key);
                    X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    serverPublicKey = kf.generatePublic(X509publicKey);
                    break;
                }
            }
        }
        dos.writeInt(Server.CLIENT_SEND_PUBLIC_KEY);
        String key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("[Client] Send key: " + key);
        dos.writeUTF(key);
    }

    public Client(String userName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.userName = userName;
        generateKeys();
        socket = new Socket(InetAddress.getByName(Server.IP_ADDRESS), Server.PORT);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        receivePublicKeyFromServer(dis, dos);
        input = new SecuredDataInputStream(dis, privateKey);
        output = new SecuredDataOutputStream(dos, serverPublicKey);
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
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | Base64DecodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void connect() throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        output.writeInt(Server.USER_CONNECT_REQUEST);
        output.writeUTF(userName);
        output.flush();
    }

    public void sendMessage(String message) throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
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
}