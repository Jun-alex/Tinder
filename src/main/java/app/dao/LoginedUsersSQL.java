package app.dao;

import app.db.Database;
import app.model.LoginedUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginedUsersSQL implements DAO<LoginedUser> {
    private final Connection conn;

    public LoginedUsersSQL() throws SQLException {
        conn = Database.mkConn();
    }

    @Override
    public List<LoginedUser> getAll() throws SQLException {
        String selectAll = "select user_id, email, password, cookie from logined_users;";
        PreparedStatement st = conn.prepareStatement(selectAll);
        ResultSet rs = st.executeQuery();

        ArrayList<LoginedUser> data = new ArrayList<>();
        while (rs.next()) {
            LoginedUser user = LoginedUser.getLoginedUserFromRs(rs);
            data.add(user);
        }
        return data;
    }

    @Override
    public Optional<LoginedUser> getById(int id) throws SQLException {
        String select = "select user_id, email, password, cookie from logined_users where user_id = ?;";
        PreparedStatement st = conn.prepareStatement(select);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if(rs.next()) {
            LoginedUser s = LoginedUser.getLoginedUserFromRs(rs);
            return Optional.of(s);
        }
        return Optional.empty();
    }

    @Override
    public void add(LoginedUser user) throws SQLException {
        String addUser = "insert into logined_users(email, password, cookie) values (?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(addUser);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getCookie());
        ps.execute();
    }

    @Override
    public void update(LoginedUser user) throws SQLException {
        String update = """
                update logined_users
                set email = ?,
                    password = ?,
                    cookie = ?,
                where user_id = ?;
                """;

        PreparedStatement ps = conn.prepareStatement(update);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getCookie());
        ps.execute();
    }

    @Override
    public void delete(int id) throws SQLException {
        String delete = "delete from logined_users where user_id = ?;";
        PreparedStatement st = conn.prepareStatement(delete);
        st.setInt(1, id);
        st.execute();
    }
    public int getLoginedUserId(String cookie) throws SQLException {
        String select = "select user_id from logined_users where cookie=?";
        PreparedStatement ps = conn.prepareStatement(select);
        ps.setString(1, cookie);
        ResultSet rs = ps.executeQuery();
        int userId = 0;
        if (rs.next()) {
            userId = rs.getInt("user_id");
        }
        return userId;
    }
}
