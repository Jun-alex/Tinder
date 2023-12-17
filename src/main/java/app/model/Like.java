package app.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Like {
    private int userId;
    private int profileId;
    private String choice;

    public Like(int userId, int profileId, String choice) {
        this.userId = userId;
        this.profileId = profileId;
        this.choice = choice;
    }

    public static Like getLikeFromRs(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        int profileId = rs.getInt("profile_id");
        String choice = rs.getString("choice");
        return new Like(userId, profileId, choice);
    }

    public int getUserId() {
        return userId;
    }

    public int getProfileId() {
        return profileId;
    }

    public String getChoice() {
        return choice;
    }
}
