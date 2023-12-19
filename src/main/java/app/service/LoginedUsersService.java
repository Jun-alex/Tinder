package app.service;

import app.dao.LoginedUsersDAO;
import app.model.LoginedUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public Optional<Integer> getLoginedUserIdByCookie(String cookie) {
        try {
            return loginedUsersDAO.getLoginedUserIdByCookie(cookie);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Optional<Integer> getLoginedUserIdByCredentials(String email, String password) {
        try {
            return loginedUsersDAO.getLoginedUserIdByCredentials(email, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean loginIsPresent(String email, String password) {
        try {
            return loginedUsersDAO.loginIsPresent(email, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateCookie(String cookie, String email, String password) {
        try {
            loginedUsersDAO.updateCookie(cookie, email, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}