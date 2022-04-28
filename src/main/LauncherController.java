package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;

public class LauncherController {

    @FXML
    public Button register;

    @FXML
    public Button login;

    @FXML
    public void goToRegister() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/register/RegisterForm.fxml"));
        Scene scene = new Scene(root);
        Launcher.getCurrentStage().setScene(scene);
        Launcher.getCurrentStage().setTitle("AllChat");
        Launcher.getCurrentStage().show();
    }

    @FXML
    public void goToLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/login/LogInForm.fxml"));
        Scene scene = new Scene(root);
        Launcher.getCurrentStage().setScene(scene);
        Launcher.getCurrentStage().setTitle("AllChat");
        Launcher.getCurrentStage().show();
    }

}
