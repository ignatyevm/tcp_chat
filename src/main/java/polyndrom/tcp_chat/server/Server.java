package polyndrom.tcp_chat.server;

import com.google.gson.*;
import javafx.util.Pair;
import polyndrom.tcp_chat.utils.Logger;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    public static final String IP_ADDRESS = "0.0.0.0";
    public static final int PORT = 12555;

    private ServerSocket serverSocket;
    private Gson gson;

    private Map<String, Pair<BufferedReader, BufferedWriter>> clients = new HashMap<>();
    private Map<String, Chat> chats = new HashMap<>();

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT, 10, InetAddress.getByName(IP_ADDRESS));
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        new Thread(() -> {
            while (true) {
                try {
                    handleRequest(serverSocket.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void handleRequest(Socket socket) {
        new Thread(() -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String requestString = input.readLine();
                Logger.logServerRequest(requestString);
                Request.Type requestType;
                {
                    Request request = gson.fromJson(requestString, Request.class);
                    requestType = request.type;
                }
                Response response;
                switch (requestType) {
                    case CREATE_CHAT: {
                        CreateChatRequest request = gson.fromJson(requestString, CreateChatRequest.class);
                        if (!chats.containsKey(request.chatName)) {
                            response = new OKResponse();
                            Chat chat = new Chat(request.chatName, request.ownerName, request.password);
                            chats.put(request.chatName, chat);
                            clients.put(request.ownerName, new Pair<>(input, output));
                        } else {
                            response = new ErrorResponse("Chat with this name already exists.");
                        }
                        output.write(gson.toJson(response));
                        output.newLine();
                    }
                    break;
                    case JOIN_CHAT: {
                        JoinChatRequest request = gson.fromJson(requestString, JoinChatRequest.class);
                        if (chats.containsKey(request.chatName)) {
                            Chat chat = chats.get(request.chatName);
                            if (chat.getPassword().equals(request.password)) {
                                response = new OKResponse();
                                clients.put(request.userName, new Pair<>(input, output));
                                UserJoinEvent event = new UserJoinEvent(request.userName);
                                sendAll(chat, gson.toJson(event));
                            } else {
                                response = new ErrorResponse("Wrong password.");
                            }
                        } else {
                            response = new ErrorResponse("Chat with this name does not exist.");
                        }
                        output.write(gson.toJson(response));
                        output.newLine();
                    }
                    break;
                    case SEND_MESSAGE: {
                        SendMessageRequest request = gson.fromJson(requestString, SendMessageRequest.class);
                        if (chats.containsKey(request.chatName)) {
                            response = new OKResponse();
                            Chat chat = chats.get(request.chatName);
                            NewMessageEvent event = new NewMessageEvent(request.userName, request.message);
                            sendAll(chat, gson.toJson(event));
                        } else {
                            response = new ErrorResponse("Chat with this name does not exist.");
                        }
                    }
                    break;
                    default: {
                        response = new ErrorResponse("Wrong request type.");
                    }
                }
                output.write(gson.toJson(response));
                output.newLine();
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendAll(Chat chat, String response) throws IOException {
        for (String memberName : chat.getUsers()) {
            getOutput(memberName).write(response);
            getOutput(memberName).newLine();
            getOutput(memberName).flush();
        }
    }

    public BufferedReader getInput(String userName) {
        return clients.get(userName).getKey();
    }

    public BufferedWriter getOutput(String userName) {
        return clients.get(userName).getValue();
    }

    public static void main(String[] args) {
        try {
            new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
