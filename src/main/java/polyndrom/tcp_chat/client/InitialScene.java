package polyndrom.tcp_chat.client;

import com.sun.org.apache.xml.internal.security.Init;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import polyndrom.tcp_chat.server.request.CreateChatRequest;

public class InitialScene extends Scene {

    public InitialScene(Group root) {
        super(root, TcpChatMain.WINDOW_WIDTH, TcpChatMain.WINDOW_HEIGHT);

        Button createChatButton = new Button("Create chat");
        createChatButton.setOnAction(event -> CreateChatScene.show());

        Button joinChatButton = new Button("Join to chat");

        HBox buttonsBox = new HBox(createChatButton, new Label("or"), joinChatButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(10);
        buttonsBox.setPrefSize(getWidth(), getHeight());

        BorderPane borderPane = new BorderPane(buttonsBox);
        borderPane.setPrefSize(getWidth(), getHeight());

        root.getChildren().addAll(borderPane);
    }

    public static void show() {
        Group root = new Group();
        TcpChatMain.getStage().setScene(new InitialScene(root));
        TcpChatMain.getStage().show();
    }

}
