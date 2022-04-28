package login;

import account.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.Launcher;

import java.io.IOException;
import java.sql.*;

public class LogInController {


    @FXML
    public Button login;

    @FXML
    public TextField username;

    @FXML
    public PasswordField password;

    @FXML
    public Button backButton;


    @FXML
    public void logInUser() throws SQLException, IOException {

        if((username.getText().equals("")) || (password.getText().equals(""))) {
            System.out.println("One of the fields are empty");
            return;
        }


        String command = "select count(*) from accounts where username = "+"'"+username.getText()+"'"+" AND password = "+"'"+password.getText()+"'";
        PreparedStatement executedCommand = Launcher.getConnection().prepareStatement(command);

        ResultSet results =  executedCommand.executeQuery(command);
        if(results.next()) {
           if(results.getString(1).equals("1")) {
               Launcher.setCurrentAccount(new Account(username.getText(),password.getText()));
               System.out.println("Logged in");

               String updateIP = "Update accounts set currentConnection = '"+Launcher.getCurrentAddress()+"' where username = '"+username.getText()+"'";
               PreparedStatement updateCommand = Launcher.getConnection().prepareStatement(updateIP);
               updateCommand.execute();

               Parent root = FXMLLoader.load(getClass().getResource("/chatWindow/chatwindow.fxml"));
               Scene scene = new Scene(root);
               Launcher.getCurrentStage().setScene(scene);
               Launcher.getCurrentStage().setTitle("AllChat");
               Launcher.getCurrentStage().show();

           }
           else
               System.out.println("Wrong username or password");
        }


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
