package polyndrom.tcp_chat.client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;

public class ChatScene extends Scene implements EventListener {

    TextField inputMessage;
    ListView<Label> messageList;

    public ChatScene(Group root) throws IOException {
        super(root, TcpChatMain.WINDOW_WIDTH, TcpChatMain.WINDOW_HEIGHT);

        setupScene(root);

        TcpChatMain.client.registerEventListener(this);
        TcpChatMain.client.connect();
        for (int i = 0; i < 100; i++) {
            // TcpChatMain.client.sendMessage("hello");
        }
    }

    private void setupScene(Group root) {
        inputMessage = new TextField();
        inputMessage.setPromptText("Type message...");
        inputMessage.setPrefSize(440, 35);
        inputMessage.setMaxSize(440, 35);
        inputMessage.setMinSize(440, 35);

        Button sendMessageButton = new Button();
        ImageView sendMessageIcon = new ImageView(getClass().getClassLoader().getResource("send.png").toString());
        sendMessageIcon.setFitWidth(30);
        sendMessageIcon.setFitHeight(30);
        sendMessageButton.setGraphic(sendMessageIcon);
        sendMessageButton.setPrefSize(60, 35);
        sendMessageButton.setMaxSize(60, 35);
        sendMessageButton.setMinSize(60, 35);

        HBox bottomBar = new HBox(inputMessage, sendMessageButton);

        messageList = new ListView();
        messageList.setPrefSize(getWidth(), getHeight() - 35);
        messageList.setMaxSize(getWidth(), getHeight() - 35);
        messageList.setMinSize(getWidth(), getHeight() - 35);

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
        String message = inputMessage.getText();
        if (!message.isEmpty()) {
            try {
                TcpChatMain.client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        inputMessage.clear();
    }

    private Label createMessageLabel(String text) {
        Label label = new Label(text);
        label.setPrefWidth(getWidth());
        label.setMaxWidth(getWidth());
        label.setMinWidth(getWidth());
        label.setTextFill(Color.BLACK);
        label.setFont(new Font(14));
        return label;
    }

    private Label createNotificationLabel(String text) {
        Label label = new Label(text);
        label.setPrefWidth(getWidth());
        label.setMaxWidth(getWidth());
        label.setMinWidth(getWidth());
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.RED);
        label.setFont(new Font(14));
        return label;
    }

    public static void show() throws IOException {
        Group root = new Group();
        TcpChatMain.getStage().setScene(new ChatScene(root));
        TcpChatMain.getStage().show();
    }

    public void putLabel(Label label) {
        Platform.runLater(() -> {
            messageList.getItems().add(label);
            messageList.scrollTo(messageList.getItems().size() - 1);
        });
    }

    @Override
    public void onUserConnected(String userName) {
        putLabel(createNotificationLabel(String.format("%s connected!", userName)));
    }

    @Override
    public void onMessageReceived(String senderName, String message) {
        Label label = createMessageLabel(String.format("%s: %s", senderName, message));
        if (TcpChatMain.client.getUserName().equals(senderName)) {
            label.setBackground(new Background(new BackgroundFill(Color.BEIGE, null, null)));
        }
        putLabel(label);
    }

    @Override
    public void onUserDisconnected(String userName) {
        putLabel(createNotificationLabel(String.format("%s disconnected!", userName)));
    }
}
