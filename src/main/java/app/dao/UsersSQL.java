package app.dao;

import app.db.Database;
import app.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersSQL implements DAO<User> {
    private final Connection conn;

    public UsersSQL() throws SQLException {
        conn = Database.mkConn();
    }

    @Override
    public List<User> getAll() throws SQLException {
        String selectAll = "select id, name, photo_url from users;";
        PreparedStatement st = conn.prepareStatement(selectAll);
        ResultSet rs = st.executeQuery();

        ArrayList<User> data = new ArrayList<>();
        while (rs.next()) {
            User user = User.getUserFromRs(rs);
            data.add(user);
        }
        return data;
    }

    @Override
    public Optional<User> getById(int id) throws SQLException {
        String select = "select id, name, photo_url from users where id = ?;";
        PreparedStatement st = conn.prepareStatement(select);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if(rs.next()) {
            User s = User.getUserFromRs(rs);
            return Optional.of(s);
        }
        return Optional.empty();
    }

    @Override
    public void add(User user) throws SQLException {
        String addUser = "insert into users(name, photo_url) values (?, ?);";
        PreparedStatement ps = conn.prepareStatement(addUser);
        ps.setString(1, user.getName());
        ps.setString(2, user.getPhotoUrl());
        ps.execute();
    }

    @Override
    public void update(User user) throws SQLException {
        String update = """
                update users
                set name = ?,
                    photo_url = ?,
                where id = ?;
                """;

        PreparedStatement st = conn.prepareStatement(update);
        st.setString(1, user.getName());
        st.setString(2, user.getPhotoUrl());
        st.setInt(3, user.getId());
        st.execute();
    }

    @Override
    public void delete(int id) throws SQLException {
        String delete = "delete from users where id = ?;";
        PreparedStatement st = conn.prepareStatement(delete);
        st.setInt(1, id);
        st.execute();
    }

    public int allProfilesQuantity() throws SQLException {
        String count = "select count(*) from users;";
        PreparedStatement st = conn.prepareStatement(count);
        ResultSet rs = st.executeQuery();
        int quantity = 0;
        if (rs.next()){
            quantity = rs.getInt("count");
        }
        return quantity;
    }
}
