package polyndrom.tcp_chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String userName;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                if (input.available() <= 0) {
                    continue;
                }
                int requestType = input.readInt();
                switch (requestType) {
                    case Server.USER_CONNECT_REQUEST: {
                        userName = input.readUTF();
                        Server.clients.put(userName, this);
                        for (ClientHandler clientHandler : Server.clients.values()) {
                            clientHandler.getOutput().writeInt(Server.USER_CONNECTED_EVENT);
                            clientHandler.getOutput().writeUTF(userName);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public DataInputStream getInput() {
        return input;
    }

    public DataOutputStream getOutput() {
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
