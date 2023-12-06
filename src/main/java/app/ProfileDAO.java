package app;

import java.util.List;

//представляет мой DAO
public interface ProfileDAO {
    List<Profile> getAll();

    Profile getById(int id);

    void add(Profile item);

    void update(Profile item);

    void delete(int id);
}
