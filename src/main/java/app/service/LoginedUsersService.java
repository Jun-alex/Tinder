package app.service;

import app.dao.LoginedUsersDAO;
import app.model.LoginedUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LoginedUsersService {
    private final LoginedUsersDAO loginedUsersDAO;

    public LoginedUsersService(Connection conn) {
        loginedUsersDAO = new LoginedUsersDAO(conn);
    }
    public List<LoginedUser> getAll() throws SQLException {
        return loginedUsersDAO.getAll();
    }
    public Optional<LoginedUser> getById(int id) throws SQLException {
        return loginedUsersDAO.getById(id);
    }
    public void add(LoginedUser user) throws SQLException {
        loginedUsersDAO.add(user);
    }
    public void update(LoginedUser user) throws SQLException {
        loginedUsersDAO.update(user);
    }
    public void delete(int id) throws SQLException {
        loginedUsersDAO.delete(id);
    }
    public int getLoginedUserId(String cookie) throws SQLException {
        return loginedUsersDAO.getLoginedUserId(cookie);
    }
}