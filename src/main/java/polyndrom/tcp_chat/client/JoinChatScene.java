package polyndrom.tcp_chat.client;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class JoinChatScene extends Scene {

    public JoinChatScene(Group root) {
        super(root, TcpChatMain.WINDOW_WIDTH, TcpChatMain.WINDOW_HEIGHT);

        Label label = new Label("Join to chat");
        label.setFont(new Font(20));

        TextField inputChatName = new TextField();
        inputChatName.setPromptText("Type chat name...");

        TextField inputUserName = new TextField();
        inputUserName.setPromptText("Type your name...");

        TextField inputChatPassword = new TextField();
        inputChatPassword.setPromptText("Type chat password...");

        Button joinButton = new Button("Join");
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> InitialScene.show());

        HBox buttonsBar = new HBox(backButton, joinButton);
        buttonsBar.setAlignment(Pos.CENTER);
        buttonsBar.setSpacing(15);

        VBox mainContainer = new VBox(label, inputChatName, inputUserName, inputChatPassword, buttonsBar);
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
        TcpChatMain.getStage().setScene(new JoinChatScene(root));
        TcpChatMain.getStage().show();
    }

}
