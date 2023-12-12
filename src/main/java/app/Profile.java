package app;

import java.sql.ResultSet;

public class Profile {
    private int id;
    private String name;
    private String photoUrl;

    // Конструктор
    public Profile(int id, String name, String photoUrl) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    // Геттери і сеттери
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
