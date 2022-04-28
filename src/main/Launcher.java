package main;

import account.Account;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

public class Launcher extends Application {

    private static Stage currentStage;
    private static Account messagedAccount;
    private static Account currentAccount;
    private static Connection connection;
    private static Socket currentClient;
    private static String currentAddress;

    public static void main(String[]args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        currentClient = new Socket("24.135.12.177",7777);
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/allchatBaza","root","1234");
        Parent root = FXMLLoader.load(getClass().getResource("/main/launcher.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("AllChat");
        stage.show();
        currentStage = stage;
        System.out.println("JavaFX Version: " + System.getProperty("javafx.version"));
        System.out.println("JavaFX Runtime Version: " + System.getProperty("javafx.runtime.version"));
        currentAddress = setCurrentAddress();

        currentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    DataOutputStream output = new DataOutputStream(currentClient.getOutputStream());
                    output.writeUTF("CLOSING CONNECTION");
                    Platform.exit();
                    System.exit(0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        });
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Socket getClient() {
        return currentClient;
    }

    public static Account getMessagedAccount() { return messagedAccount; }

    public static void setMessagedAccount(Account messagedAccount) { Launcher.messagedAccount = messagedAccount; }

    public static Stage getCurrentStage() {
        return currentStage;
    }

    public static void setCurrentAccount(Account currentAccount) {
        Launcher.currentAccount = currentAccount;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static String setCurrentAddress() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com/");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        return br.readLine();
    }

    public static String getCurrentAddress() {
        return currentAddress;
    }
}
