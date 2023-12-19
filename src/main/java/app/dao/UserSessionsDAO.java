package app.dao;

import app.model.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserSessionsDAO implements DAO<UserSession>{
    private final Connection conn;
    public UserSessionsDAO(Connection conn) {
        this.conn = conn;
    }
    @Override
    public List<UserSession> getAll() throws SQLException {
        String selectAll = "select email, password, cookie from sessions;";
        PreparedStatement st = conn.prepareStatement(selectAll);
        ResultSet rs = st.executeQuery();

        ArrayList<UserSession> data = new ArrayList<>();
        while (rs.next()) {
            UserSession session = UserSession.getSessionFromRs(rs);
            data.add(session);
        }
        return data;
    }

    @Override
    public Optional<UserSession> getById(int id) throws SQLException {
        String select = "select email, password, cookie from sessions where session_id = ?;";
        PreparedStatement st = conn.prepareStatement(select);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if(rs.next()) {
            UserSession s = UserSession.getSessionFromRs(rs);
            return Optional.of(s);
        }
        return Optional.empty();
    }

    @Override
    public void add(UserSession userSession) throws SQLException {
        String addUser = "insert into sessions(email, password, cookie) values (?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(addUser);
        ps.setString(1, userSession.email());
        ps.setString(2, userSession.password());
        ps.setString(3, userSession.cookie());
        ps.execute();
    }

    @Override
    public void update(UserSession userSession) throws SQLException {
        String update = """
                update sessions
                set cookie = ?,
                where email = ? and password = ?;
                """;

        PreparedStatement ps = conn.prepareStatement(update);
        ps.setString(1, userSession.cookie());
        ps.setString(2, userSession.email());
        ps.setString(2, userSession.password());
        ps.execute();
    }

    @Override
    public void delete(int id) throws SQLException {
        String delete = "delete from sessions where session_id = ?;";
        PreparedStatement st = conn.prepareStatement(delete);
        st.setInt(1, id);
        st.execute();
    }

    public boolean cookieIsPresent(String email, String password, String cookie) throws SQLException {
        String select = """
                select email, password, cookie from sessions
                    where email = ? and password = ? and cookie = ?
                """;
        PreparedStatement ps = conn.prepareStatement(select);
        ps.setString(1, email);
        ps.setString(2, password);
        ps.setString(3, cookie);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}
