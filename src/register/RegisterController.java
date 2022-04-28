package register;

import java.io.IOException;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.Launcher;

public class RegisterController {

    @FXML
    public Button registerButton;

    @FXML
    public TextField username;

    @FXML
    public PasswordField password;

    @FXML
    public Button backButton;

    @FXML
    public void registerUser() throws Exception {
        if((username.getText().equals("")) || (password.getText().equals(""))) {
            System.out.println("One of the fields are empty");
            return;
        }


        String command = "INSERT INTO accounts(username,password,currentConnection)"+"VALUES(?, ?, ?)";

        PreparedStatement executedCommand = Launcher.getConnection().prepareStatement(command);
        executedCommand.setString(1,username.getText());
        executedCommand.setString(2,password.getText());
        executedCommand.setString(3,Launcher.getCurrentAddress());

        executedCommand.execute();
        goToLogin();
    }


    public void goToLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/login/LogInForm.fxml"));
        Scene scene = new Scene(root);
        Launcher.getCurrentStage().setScene(scene);
        Launcher.getCurrentStage().setTitle("AllChat");
        Launcher.getCurrentStage().show();
    }

    @FXML
    public void goBack() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/main/launcher.fxml"));
        Scene scene = new Scene(root);
        Launcher.getCurrentStage().setScene(scene);
        Launcher.getCurrentStage().setTitle("AllChat");
        Launcher.getCurrentStage().show();
    }

}
