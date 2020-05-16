package polyndrom.tcp_chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (input.available() <= 0) {
                    continue;
                }
                int requestType = input.readInt();;
                switch (requestType) {
                    case Server.USER_CONNECT_REQUEST: {
                        String userName = input.readUTF();
                        Server.clients.put(userName, this);
                        for (ClientHandler clientHandler : Server.clients.values()) {
                            clientHandler.getOutput().writeInt(Server.NEW_USER_EVENT);
                            clientHandler.getOutput().writeUTF(userName);
                        }
                        System.out.println("Connect: " + userName);
                    }
                    break;
                    case Server.USER_SEND_MESSAGE_REQUEST: {
                        String userName = input.readUTF();
                        String message = input.readUTF();
                        for (ClientHandler clientHandler : Server.clients.values()) {
                            clientHandler.getOutput().writeInt(Server.NEW_MESSAGE_EVENT);
                            clientHandler.getOutput().writeUTF(userName);
                            clientHandler.getOutput().writeUTF(message);
                            clientHandler.getOutput().flush();
                        }
                        System.out.println(userName + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}
