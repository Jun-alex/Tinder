package app.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Profile(int id, String name, String photoUrl) {
    public static Profile getUserFromRs(ResultSet rs) throws SQLException {
        int userId = rs.getInt("id");
        String userName = rs.getString("name");
        String userPhotoUrl = rs.getString("photo_url");
        return new Profile(userId, userName, userPhotoUrl);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name=%s, photoUrl=%s", id, name, photoUrl);
    }
}