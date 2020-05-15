package polyndrom.tcp_chat.client;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class TcpChatMain extends Application {

    public static final int WINDOW_HEIGHT = 400;
    public static final int WINDOW_WIDTH = 500;

    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        TcpChatMain.stage = stage;
        stage.setTitle("TcpChat");
        stage.setResizable(false);
        stage.centerOnScreen();
        InitialScene.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public static Stage getStage() {
        return TcpChatMain.stage;
    }

}
