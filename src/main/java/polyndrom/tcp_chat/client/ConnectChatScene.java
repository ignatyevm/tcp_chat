package polyndrom.tcp_chat.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ConnectChatScene extends Scene {

    public ConnectChatScene(Group root) {
        super(root, TcpChatMain.WINDOW_WIDTH, TcpChatMain.WINDOW_HEIGHT);

        getStylesheets().add("main.css");

        Label label = new Label("Connect to chat");
        label.setFont(new Font(20));

        JFXTextField inputUserName = new JFXTextField();
        inputUserName.setPromptText("Type your name...");

        JFXButton connectButton = new JFXButton("Connect");
        connectButton.getStyleClass().add("primary-button");
        connectButton.setPrefWidth(200);
        connectButton.setMaxWidth(200);
        connectButton.setMinWidth(200);
        connectButton.setOnAction(event -> {
            tryConnect(inputUserName.getText().trim());
        });
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                tryConnect(inputUserName.getText().trim());
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

    public void tryConnect(String userName) {
        try {
            if (!userName.isEmpty()) {
                TcpChatMain.client = new Client(userName);
                ChatScene.show();
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | InvalidKeyException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setHeaderText("Unknown error");
            alert.show();
            e.printStackTrace();
        }
    }

    public static void show() {
        Group root = new Group();
        TcpChatMain.getStage().setScene(new ConnectChatScene(root));
        TcpChatMain.getStage().show();
    }

}
