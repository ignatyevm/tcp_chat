package polyndrom.tcp_chat.client;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class TcpChatMain extends Application {

    public static final int WINDOW_HEIGHT = 400;
    public static final int WINDOW_WIDTH = 500;

    private static Stage stage;

    public static Client client;

    @Override
    public void start(Stage stage) {
        TcpChatMain.stage = stage;
        stage.setOnCloseRequest(event -> {
            if (client != null && client.isConnected()) {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        stage.setTitle("Tcp Chat");
        stage.setResizable(false);
        stage.centerOnScreen();
        ConnectChatScene.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public static Stage getStage() {
        return TcpChatMain.stage;
    }

}
