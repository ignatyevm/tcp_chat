package polyndrom.tcp_chat.client;

import com.sun.org.apache.xml.internal.security.Init;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import polyndrom.tcp_chat.server.request.CreateChatRequest;

public class CreateChatScene extends Scene {

    public CreateChatScene(Group root) {
        super(root, TcpChatMain.WINDOW_WIDTH, TcpChatMain.WINDOW_HEIGHT);

        TextField inputChatName = new TextField();
        inputChatName.setPromptText("Type chat name...");

        TextField inputUserName = new TextField();
        inputUserName.setPromptText("Type your name...");

        TextField inputChatPassword = new TextField();
        inputChatPassword.setPromptText("Type chat password...");

        Button createButton = new Button("Create");
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> InitialScene.show());

        VBox mainContainer = new VBox(inputChatName, inputUserName, inputChatPassword, createButton);
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
        TcpChatMain.getStage().setScene(new CreateChatScene(root));
        TcpChatMain.getStage().show();
    }

}
