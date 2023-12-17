package app.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int id;
    private String name;
    private String photoUrl;

    public User(int id, String name, String photoUrl) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public User() { }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public static User getUserFromRs(ResultSet rs) throws SQLException {
        int userId = rs.getInt("id");
        String userName = rs.getString("name");
        String userPhotoUrl = rs.getString("photo_url");
        return new User(userId, userName, userPhotoUrl);
    }
    @Override
    public String toString() {
        return String.format("User{id=%d, name=%s, photoUrl=%s", id, name, photoUrl);
    }
}
