package chatWindow;


import account.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import main.Launcher;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ChatController implements Initializable {


        @FXML
        private Label yourChatsLabel;

        @FXML
        private Label findPeopleLabel;

        @FXML
        private Button findButton;

        @FXML
        private ListView<String> chatsList;

        @FXML
        private Label username;

        @FXML
        private TextField searchedUser;


        @Override
        public void initialize(URL location, ResourceBundle resources) {
            while(Launcher.getCurrentAccount() == null) {}
            username.setText(Launcher.getCurrentAccount().getUsername());
            try {
                loadChatList();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @FXML
        public void findUser() throws SQLException, IOException {
            String command = "select count(*) from accounts where username = "+"'"+searchedUser.getText()+"'";
            PreparedStatement statement = Launcher.getConnection().prepareStatement(command);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                if(result.getString(1).equals("1")) {
                    Launcher.setMessagedAccount(new Account(searchedUser.getText(),"XXXX"));
                    goToChat();
                }
            }
        }

        @FXML
        public void selectChat() throws IOException {
            if(chatsList.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            Launcher.setMessagedAccount(new Account(chatsList.getSelectionModel().getSelectedItem(),"XXXX"));
            goToChat();
        }


        public void loadChatList() throws SQLException {
            String command  = "select distinct id_to,accounts.username from messages join accounts on id_to = accounts.id  where id_from = "+Launcher.getCurrentAccount().obtainID();
            PreparedStatement preparedStatement = Launcher.getConnection().prepareStatement(command);

            ResultSet messagedUsernames = preparedStatement.executeQuery();

            while(messagedUsernames.next()) {
                chatsList.getItems().add(messagedUsernames.getString(2));
            }

        }

    public void goToChat() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/chatWindow/messageWindow.fxml"));
        Scene scene = new Scene(root);
        Launcher.getCurrentStage().setScene(scene);
        Launcher.getCurrentStage().setTitle("AllChat");
        Launcher.getCurrentStage().show();
    }
}
