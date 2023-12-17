package app.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginedUser {
    private int id;
    private String email;
    private String password;
    private String cookie;
    public LoginedUser(String email, String password, String cookie) {
        this.email = email;
        this.password = password;
        this.cookie = cookie;
    }
    public LoginedUser(int id, String email, String password, String cookie) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.cookie = cookie;
    }

    public int getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public String getCookie() {
        return cookie;
    }
    public static LoginedUser getLoginedUserFromRs(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String userEmail = rs.getString("email");
        String password = rs.getString("password");
        String cookie = rs.getString("cookie");
        return new LoginedUser(userId, userEmail, password, cookie);
    }
}
