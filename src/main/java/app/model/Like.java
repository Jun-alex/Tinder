package app.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Like(int userId, int profileId, String choice)  {

    public static Like getLikeFromRs(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        int profileId = rs.getInt("profile_id");
        String choice = rs.getString("choice");
        return new Like(userId, profileId, choice);
    }
}