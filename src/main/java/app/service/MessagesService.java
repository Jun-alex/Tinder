package app.service;

import app.dao.LoginedUsersDAO;
import app.dao.MessagesDAO;
import app.model.Message;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MessagesService {
    private final MessagesDAO messagesDAO;

    public MessagesService(Connection conn) {
        messagesDAO = new MessagesDAO(conn);
    }
    public List<Message> getMessagesForParticularChat(int user_id_from, int user_id_to) throws SQLException {
        return messagesDAO.getMessagesForParticularChat(user_id_from, user_id_to);
    }
    public List<Message> getAll() throws SQLException {
        return messagesDAO.getAll();
    }
    public Optional<Message> getById(int id) throws SQLException {
        return messagesDAO.getById(id);
    }
    public void add(Message msg) throws SQLException {
        messagesDAO.add(msg);
    }
    public void update(Message msg) throws SQLException {
        messagesDAO.update(msg);
    }
    public void delete(int id) throws SQLException {
        messagesDAO.delete(id);
    }
}