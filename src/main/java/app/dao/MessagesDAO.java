package app.dao;

import app.model.Message;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessagesDAO implements DAO<Message>{
    private final Connection conn;
    public MessagesDAO(Connection conn) {
        this.conn = conn;
    }
    public List<Message> getMessagesForParticularChat(int user_id_from, int user_id_to) throws SQLException {
        String sql = "select * from messages where user_id_from=? and user_id_to=?";
        List<Message> messages = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, user_id_from);
        ps.setInt(2, user_id_to);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Message msg = new Message(rs.getInt("user_id_from")
                    , rs.getInt("user_id_to")
                    , rs.getString("message")
                    , rs.getTimestamp("time").toInstant().atZone(ZoneId.systemDefault()));
            messages.add(msg);
        }
        return messages;
    }

    @Override
    public List<Message> getAll() throws SQLException {
        String selectAll = "select user_id_from, user_id_to, message, time from messages;";
        PreparedStatement st = conn.prepareStatement(selectAll);
        ResultSet rs = st.executeQuery();

        ArrayList<Message> data = new ArrayList<>();
        while (rs.next()) {
            int user_id_from = rs.getInt("user_id_from");
            int user_id_to = rs.getInt("user_id_to");
            String message = rs.getString("message");
            ZonedDateTime time = rs.getTimestamp("time").toInstant().atZone(ZoneId.systemDefault());
            Message msg = new Message(user_id_from, user_id_to, message, time);
            data.add(msg);
        }
        return data;
    }

    @Override
    public Optional<Message> getById(int id) throws SQLException {
        String select = "select * from messages where message_id=? ";
        PreparedStatement ps = conn.prepareStatement(select);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            Message msg = new Message(rs.getInt("user_id_from")
                    , rs.getInt("user_id_to")
                    , rs.getString("message")
                    , rs.getTimestamp("time").toInstant().atZone(ZoneId.systemDefault()));
            return Optional.of(msg);
        }
        return Optional.empty();
    }

    @Override
    public void add(Message msg) throws SQLException {
        String insert = "insert into messages(user_id_from, user_id_to, message, time) VALUES(?,?,?,?)";

//        Отримуємо timestamp у мілісекундах
        Timestamp timestamp = new Timestamp(msg.time().toInstant().getEpochSecond() * 1000L);
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setInt(1, msg.userIdFrom());
        ps.setInt(2, msg.userIdTo());
        ps.setString(3, msg.message());
        ps.setTimestamp(4, timestamp);
        ps.execute();
    }

    @Override
    public void update(Message msg) throws SQLException {
        String update = """
                update messages
                set message = ?,
                where user_id_from = ? and user_id_to = ?;
                """;

        PreparedStatement st = conn.prepareStatement(update);
        st.setString(1, msg.message());
        st.setInt(2, msg.userIdFrom());
        st.setInt(3, msg.userIdTo());
        st.execute();
    }

    @Override
    public void delete(int id) throws SQLException {
        String delete = "delete from messages where message_id = ?;";
        PreparedStatement st = conn.prepareStatement(delete);
        st.setInt(1, id);
        st.execute();
    }
}