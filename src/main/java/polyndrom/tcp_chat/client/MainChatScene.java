package polyndrom.tcp_chat.client;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MainChatScene extends Scene {

    private ListView<Label> messageList;

    public MainChatScene(Group root) {
        super(root, TcpChatMain.WINDOW_WIDTH, TcpChatMain.WINDOW_HEIGHT);

        TextField inputMessage = new TextField();
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

        ListView<Label> messageList = new ListView<>();
        messageList.setPrefSize(getWidth(), getHeight() - 35);
        messageList.setMaxSize(getWidth(), getHeight() - 35);
        messageList.setMinSize(getWidth(), getHeight() - 35);

        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage(inputMessage.getText());
                inputMessage.clear();
            }
        });

        sendMessageButton.setOnAction(event -> {
            sendMessage(inputMessage.getText());
            inputMessage.clear();
        });

        BorderPane borderPane = new BorderPane(messageList);
        borderPane.setBottom(bottomBar);
        borderPane.setPrefSize(getWidth(), getHeight());

        root.getChildren().addAll(borderPane);
    }

    void sendMessage(String message) {
        if (!message.isEmpty()) {
            // do stuff
        }
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
        label.setFont(new Font(12));
        return label;
    }

    public static void show() {
        Group root = new Group();
        TcpChatMain.getStage().setScene(new MainChatScene(root));
        TcpChatMain.getStage().show();
    }

}
