package polyndrom.tcp_chat.client;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;

public class ConnectChatScene extends Scene {

    public ConnectChatScene(Group root) {
        super(root, TcpChatMain.WINDOW_WIDTH, TcpChatMain.WINDOW_HEIGHT);

        Label label = new Label("Connect to chat");
        label.setFont(new Font(20));

        TextField inputUserName = new TextField();
        inputUserName.setPromptText("Type your name...");

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(event -> {
            try {
                TcpChatMain.client = new Client(inputUserName.getText().trim());
                ChatScene.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    TcpChatMain.client = new Client(inputUserName.getText().trim());
                    ChatScene.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        VBox mainContainer = new VBox(label, inputUserName, connectButton);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(10);
        mainContainer.setPrefSize(200, getHeight());
        mainContainer.setMaxSize(200, getHeight());
        mainContainer.setMinSize(200, getHeight());

        BorderPane borderPane = new BorderPane(mainContainer);
        borderPane.setPrefSize(getWidth(), getHeight());

        root.getChildren().addAll(borderPane);
    }

    public static void show() {
        Group root = new Group();
        TcpChatMain.getStage().setScene(new ConnectChatScene(root));
        TcpChatMain.getStage().show();
    }

}
