package app.service;

import app.dao.LoginedUsersDAO;
import app.dao.UserChoicesDAO;
import app.model.Like;
import app.model.Profile;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserChoicesService {
    private final UserChoicesDAO userChoicesDAO;

    public UserChoicesService(Connection conn) {
        userChoicesDAO = new UserChoicesDAO(conn);
    }
    public List<Like> getAll() throws SQLException {
        return userChoicesDAO.getAll();
    }
    public Optional<Like> getById(int id) throws SQLException {
        return userChoicesDAO.getById(id);
    }
    public void add(Like like) throws SQLException {
        userChoicesDAO.add(like);
    }
    public void update(Like like) throws SQLException {
        userChoicesDAO.update(like);
    }
    public void delete(int id) throws SQLException {
        userChoicesDAO.delete(id);
    }
    public Optional<Integer> getMaxProfileId() throws SQLException {
        return userChoicesDAO.getMaxProfileId();
    }
    public List<Profile> getLikedProfiles() throws SQLException {
        return userChoicesDAO.getLikedProfiles();
    }
}