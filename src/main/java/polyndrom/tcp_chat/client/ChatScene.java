package polyndrom.tcp_chat.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.io.IOException;

public class ChatScene extends Scene implements EventListener {

    JFXTextField inputMessage;
    JFXListView<Message> messageList;

    public ChatScene(Group root) throws IOException {
        super(root, TcpChatMain.WINDOW_WIDTH, TcpChatMain.WINDOW_HEIGHT);

        getStylesheets().add("main.css");

        setupScene(root);

        TcpChatMain.client.registerEventListener(this);
        TcpChatMain.client.connect();
    }

    private void setupScene(Group root) {
        inputMessage = new JFXTextField();
        inputMessage.setPromptText("Type message...");
        inputMessage.setPrefSize(405, 30);
        inputMessage.setMaxSize(405, 30);
        inputMessage.setMinSize(405, 30);
        Platform.runLater(() -> inputMessage.requestFocus());

        JFXButton sendMessageButton = new JFXButton();
        ImageView sendMessageIcon = new ImageView(getClass().getClassLoader().getResource("send.png").toString());
        sendMessageIcon.setFitWidth(25);
        sendMessageIcon.setFitHeight(20);
        sendMessageButton.setGraphic(sendMessageIcon);
        sendMessageButton.setPrefSize(50, 28);
        sendMessageButton.setMaxSize(50, 28);
        sendMessageButton.setMinSize(50, 28);
        sendMessageButton.setPadding(new Insets(1, 0, 1, 0));
        sendMessageButton.getStyleClass().add("primary-button");

        HBox bottomBar = new HBox(inputMessage, sendMessageButton);
        bottomBar.setSpacing(15);
        bottomBar.setPadding(new Insets(10, 15, 10, 15));
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.getStyleClass().add("top-border");

        messageList = new JFXListView();
        messageList.setPrefSize(getWidth(), getHeight() - 50);
        messageList.setMaxSize(getWidth(), getHeight() - 50);
        messageList.setMinSize(getWidth(), getHeight() - 50);
        messageList.setCellFactory(list -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) return;
                if (item.type == Message.Type.NOTIFICATION) {
                    System.out.println("pidor");
                    pseudoClassStateChanged(PseudoClass.getPseudoClass("notification"), true);
                } else {
                    System.out.println("lox");
                    pseudoClassStateChanged(PseudoClass.getPseudoClass("notification"), false);
                }
                setText(item.text);
            }
        });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                trySendMessage();
            }
        });

        sendMessageButton.setOnAction(event -> {
            trySendMessage();
        });

        BorderPane borderPane = new BorderPane(messageList);
        borderPane.setBottom(bottomBar);
        borderPane.setPrefSize(getWidth(), getHeight());

        root.getChildren().addAll(borderPane);
    }

    void trySendMessage() {
        String message = inputMessage.getText().trim();
        if (!message.isEmpty()) {
            try {
                TcpChatMain.client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        inputMessage.clear();
    }

    public static void show() throws IOException {
        Group root = new Group();
        TcpChatMain.getStage().setScene(new ChatScene(root));
        TcpChatMain.getStage().show();
    }

    public void addNodeToList(Message.Type type, String message) {
        Platform.runLater(() -> {
            messageList.getItems().add(new Message(type, message));
            messageList.scrollTo(messageList.getItems().size() - 1);
        });
    }

    public void addMessageToList(String message) {
        addNodeToList(Message.Type.MESSAGE, message);
    }

    public void addNotificationToList(String message) {
        addNodeToList(Message.Type.NOTIFICATION, message);
    }

    @Override
    public void onUserConnected(String userName) {
        addNotificationToList(String.format("%s connected!", userName));
    }

    @Override
    public void onMessageReceived(String senderName, String message) {
        addMessageToList(String.format("%s: %s", senderName, message));
    }

    @Override
    public void onUserDisconnected(String userName) {
        addNotificationToList(String.format("%s disconnected!", userName));
    }

    static class Message {
        enum Type { MESSAGE, NOTIFICATION }
        Type type;
        String text;
        public Message(Type type, String text) {
            this.type = type;
            this.text = text;
        }
    }
}