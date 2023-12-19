package app.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public record UserSession(String email, String password, String cookie) {
    public static UserSession getSessionFromRs(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        String password = rs.getString("password");
        String cookie = rs.getString("cookie");
        return new UserSession(email, password, cookie);
    }
}
