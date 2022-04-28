package chatWindow;

import account.Account;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import main.Launcher;
import message.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MessageController implements Initializable {

    @FXML
    private TextField messageBar;

    /*
    @FXML
    private ListView messages;
    */

    @FXML
    private Label yourChatsLabel;

    @FXML
    private Button sendButton;

    @FXML
    private Label username;

    @FXML
    private Label messagingUser;

    @FXML
    private ScrollPane messagesContainer;

    @FXML
    private VBox messages;

    @FXML
    private ListView<String> chatList;

    private static ArrayList<Label> messageBoxes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        username.setText(Launcher.getCurrentAccount().getUsername());
        messagingUser.setText(Launcher.getMessagedAccount().getUsername());
        messageBoxes.clear();

        try {
            loadMessages();
            loadChatList();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        messageBar.setOnKeyPressed(keyEvent ->  {
            if( keyEvent.getCode() == KeyCode.ENTER ) {
                try {
                    sendMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


        try {
            startReceiver();
        } catch (IOException e) {
            e.printStackTrace();
        }

        messagesContainer.setVvalue(messagesContainer.getVmax());

    }

    @FXML
    public void sendMessage() throws IOException, SQLException {
        DataOutputStream sender = new DataOutputStream(Launcher.getClient().getOutputStream());
        Message message = new Message(messagingUser.getText(), username.getText(), messageBar.getText());


        String command = "select currentConnection from accounts where username = '" + messagingUser.getText() + "'";
        PreparedStatement getIp = Launcher.getConnection().prepareStatement(command);
        ResultSet result = getIp.executeQuery(command);
        String ip = null;
        if (result.next()) {
            ip = result.getString(1);
        }


        sender.writeUTF(Launcher.getCurrentAccount().getUsername() + "_" + message.getContent() + "_" + ip + "_" + Launcher.getMessagedAccount().getUsername());
        messageBoxes.add(new Label("[" + Launcher.getCurrentAccount().getUsername() + "] : " + message.getContent()));
        messageBoxes.get(messageBoxes.size()-1).getStylesheets().addAll("/chatWindow/visualCandy/MessageBoxes.css");
        messages.getChildren().clear();
        messages.getChildren().addAll(messageBoxes);
        messagesContainer.setContent(messages);


        String addMessage = "INSERT INTO messages (id_from,id_to,message_content) VALUES(?,?,?)";

        PreparedStatement executedCommand = Launcher.getConnection().prepareStatement(addMessage);
        executedCommand.setString(1, String.valueOf(Launcher.getCurrentAccount().obtainID()));
        executedCommand.setString(2, String.valueOf(Launcher.getMessagedAccount().obtainID()));
        executedCommand.setString(3, message.getContent());
        executedCommand.execute();

        messageBar.clear();
        messagesContainer.setVvalue(messagesContainer.getVmax());
    }

    @FXML
    public void selectChat() throws IOException {
        if(chatList.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        Launcher.setMessagedAccount(new Account(chatList.getSelectionModel().getSelectedItem(),"XXXX"));
        goToChat();
    }

    public void goToChat() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/chatWindow/messageWindow.fxml"));
        Scene scene = new Scene(root);
        Launcher.getCurrentStage().setScene(scene);
        Launcher.getCurrentStage().setTitle("AllChat");
        Launcher.getCurrentStage().show();
    }

    public void loadChatList() throws SQLException {
        String command  = "select distinct id_to,accounts.username from messages join accounts on id_to = accounts.id  where id_from = "+Launcher.getCurrentAccount().obtainID();
        PreparedStatement preparedStatement = Launcher.getConnection().prepareStatement(command);

        ResultSet messagedUsernames = preparedStatement.executeQuery();

        while(messagedUsernames.next()) {
            chatList.getItems().add(messagedUsernames.getString(2));
        }
    }

    public void loadMessages() throws SQLException {
        String fetchCommand = "select username,message_content from messages join accounts on id_from = accounts.id where (id_from = "+Launcher.getCurrentAccount().obtainID()+" and id_to = "
                +Launcher.getMessagedAccount().obtainID()+") or (id_from = "+Launcher.getMessagedAccount().obtainID()+" and id_to = "+Launcher.getCurrentAccount().obtainID()+") order by message_id asc";

        PreparedStatement prepareFetch = Launcher.getConnection().prepareStatement(fetchCommand);
        ResultSet rows = prepareFetch.executeQuery();

        while (rows.next()) {
            messageBoxes.add(new Label("["+rows.getString(1)+"] : "+rows.getString(2)));
        }

        messageBoxes.forEach(label -> label.getStylesheets().addAll("/chatWindow/visualCandy/MessageBoxes.css"));

        messages.getChildren().addAll(messageBoxes);
        messagesContainer.setContent(messages);

    }


    private void startReceiver() throws IOException {

        new Thread(() -> {
            try {
                DataInputStream input = new DataInputStream(Launcher.getClient().getInputStream());

                while (true) {
                    String packet = input.readUTF();
                    String packetArgs[] = packet.split("_");

                    if(packetArgs.length == 2) {
                        if(packetArgs[1].equals(Launcher.getCurrentAccount().getUsername())) {
                            messageBoxes.add(new Label("[" + Launcher.getMessagedAccount().getUsername() + "] : " + packetArgs[0]));
                            messageBoxes.get(messageBoxes.size()-1).getStylesheets().addAll("/chatWindow/visualCandy/MessageBoxes.css");
                            Platform.runLater(() -> {
                                messages.getChildren().add(messageBoxes.get(messageBoxes.size()-1));
                                messagesContainer.setContent(messages);
                            });
                        }
                        else {
                            continue;
                        }
                    }


                    else  {
                        continue;
                    }

                }


            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }).start();


    }

}


//DOSTA SPORIJE NADJEN EFEKTIVNIJI NACIN U SQL JEZIKU
/*
        while (rows.next()) {
            if (rows.getString(1).equals(String.valueOf(Launcher.getMessagedAccount().obtainID())) && (rows.getString(3).equals(String.valueOf(Launcher.getCurrentAccount().obtainID())))
                    || (rows.getString(3).equals(String.valueOf(Launcher.getMessagedAccount().obtainID())) && (rows.getString(1).equals(String.valueOf(Launcher.getCurrentAccount().obtainID()))))) {

                if (rows.getString(1).equals(String.valueOf(Launcher.getCurrentAccount().obtainID()))) {
                    messageBoxes.add(new Label("[" + Launcher.getCurrentAccount().getUsername() + "] : " + rows.getString(2)));
                } else {
                    messageBoxes.add(new Label("[" + Launcher.getMessagedAccount().getUsername() + "] : " + rows.getString(2)));
                }


            }
        }
        */
