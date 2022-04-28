package account;

import main.Launcher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {

    private String username;
    private String password;

    public Account(String username,String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public int obtainID() throws SQLException {

        String command = "select id from accounts where username = '"+username+"'";
        PreparedStatement executedCommand = Launcher.getConnection().prepareStatement(command);
        ResultSet resultSet = executedCommand.executeQuery();
        if(resultSet.next()) {
            return resultSet.getInt(1);
        }
        else {
            return -1;
        }

    }

}
