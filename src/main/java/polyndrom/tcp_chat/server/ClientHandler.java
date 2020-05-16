package polyndrom.tcp_chat.server;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ClientHandler implements Runnable {

    private Socket socket;
    private SecuredDataInputStream input;
    private SecuredDataOutputStream output;
    private String userName;
    private PublicKey clientPublicKey;

    public ClientHandler(Socket socket) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.socket = socket;
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        receivePublicKeyFromClient(dis, dos);
        input = new SecuredDataInputStream(dis, Server.privateKey);
        output = new SecuredDataOutputStream(dos, clientPublicKey);
    }

    public void receivePublicKeyFromClient(DataInputStream dis, DataOutputStream dos) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        dos.writeInt(Server.SERVER_SEND_PUBLIC_KEY);
        String key1 = Base64.getEncoder().encodeToString(Server.publicKey.getEncoded());
        System.out.println("[Server] Send key: " + key1);
        dos.writeUTF(key1);
        while (true) {
            if (dis.available() > 0) {
                int requestId = dis.readInt();
                if (requestId == Server.CLIENT_SEND_PUBLIC_KEY) {
                    String key = dis.readUTF();
                    System.out.println("[Server] Client key: " + key);
                    byte[] byteKey = Base64.getDecoder().decode(key);
                    X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    clientPublicKey = kf.generatePublic(X509publicKey);
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                if (input.available() <= 0) {
                    continue;
                }
                int requestType = input.readInt();
                System.out.println(requestType);
                System.out.println(input.available());
                switch (requestType) {
                    case Server.USER_CONNECT_REQUEST: {
                        userName = input.readUTF();
                        Server.clients.put(userName, this);
                        for (ClientHandler clientHandler : Server.clients.values()) {
                            clientHandler.getOutput().writeInt(Server.USER_CONNECTED_EVENT);
                            clientHandler.getOutput().writeUTF(userName);
                            clientHandler.getOutput().flush();
                        }
                        System.out.println("Connected: " + userName);
                    }
                    break;
                    case Server.USER_SEND_MESSAGE_REQUEST: {
                        String userName = input.readUTF();
                        String message = input.readUTF();
                        for (ClientHandler clientHandler : Server.clients.values()) {
                            clientHandler.getOutput().writeInt(Server.MESSAGE_RECEIVED_EVENT);
                            clientHandler.getOutput().writeUTF(userName);
                            clientHandler.getOutput().writeUTF(message);
                        }
                        System.out.println(userName + ": " + message);
                    }
                }
            } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | Base64DecodingException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public SecuredDataInputStream getInput() {
        return input;
    }

    public SecuredDataOutputStream getOutput() {
        return output;
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}
