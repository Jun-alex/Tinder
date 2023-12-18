package app.service;

import app.dao.LoginedUsersDAO;
import app.dao.ProfilesDAO;
import app.model.Profile;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProfilesService {
    private final ProfilesDAO profilesDAO;

    public ProfilesService(Connection conn) {
        profilesDAO = new ProfilesDAO(conn);
    }
    public List<Profile> getAll() throws SQLException {
        return profilesDAO.getAll();
    }
    public Optional<Profile> getById(int id) throws SQLException {
        return profilesDAO.getById(id);
    }
    public void add(Profile profile) throws SQLException {
        profilesDAO.add(profile);
    }
    public void update(Profile profile) throws SQLException {
        profilesDAO.update(profile);
    }
    public void delete(int id) throws SQLException {
        profilesDAO.delete(id);
    }
    public int allProfilesQuantity() throws SQLException {
        return profilesDAO.allProfilesQuantity();
    }
}