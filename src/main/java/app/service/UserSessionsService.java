package app.service;

import app.dao.UserSessionsDAO;
import app.model.UserSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserSessionsService {
    private final UserSessionsDAO userSessionsDAO;
    public  UserSessionsService(Connection conn) {
        userSessionsDAO = new UserSessionsDAO(conn);
    }
    public List<UserSession> getAll() throws SQLException {
        return userSessionsDAO.getAll();
    }
    public Optional<UserSession> getById(int id) throws SQLException {
        return userSessionsDAO.getById(id);
    }
    public void add(UserSession userSession) throws SQLException {
        userSessionsDAO.add(userSession);
    }
    public void update(UserSession userSession) throws SQLException {
        userSessionsDAO.update(userSession);
    }
    public void delete(int id) throws SQLException {
        userSessionsDAO.delete(id);
    }
    public boolean cookieIsPresent(String email, String password, String cookie) throws SQLException {
        return userSessionsDAO.cookieIsPresent(email, password, cookie);
    }
}