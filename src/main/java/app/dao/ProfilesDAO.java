package app.dao;

import app.model.Profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfilesDAO implements DAO<Profile> {
    private final Connection conn;

    public ProfilesDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Profile> getAll() throws SQLException {
        String selectAll = "select id, name, photo_url from profiles;";
        PreparedStatement st = conn.prepareStatement(selectAll);
        ResultSet rs = st.executeQuery();

        ArrayList<Profile> data = new ArrayList<>();
        while (rs.next()) {
            Profile profile = Profile.getUserFromRs(rs);
            data.add(profile);
        }
        return data;
    }

    @Override
    public Optional<Profile> getById(int id) throws SQLException {
        String select = "select id, name, photo_url from profiles where id = ?;";
        PreparedStatement st = conn.prepareStatement(select);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if(rs.next()) {
            Profile s = Profile.getUserFromRs(rs);
            return Optional.of(s);
        }
        return Optional.empty();
    }

    @Override
    public void add(Profile profile) throws SQLException {
        String addUser = "insert into profiles(name, photo_url) values (?, ?);";
        PreparedStatement ps = conn.prepareStatement(addUser);
        ps.setString(1, profile.name());
        ps.setString(2, profile.photoUrl());
        ps.execute();
    }

    @Override
    public void update(Profile profile) throws SQLException {
        String update = """
                update profiles
                set name = ?,
                    photo_url = ?,
                where id = ?;
                """;

        PreparedStatement st = conn.prepareStatement(update);
        st.setString(1, profile.name());
        st.setString(2, profile.photoUrl());
        st.setInt(3, profile.id());
        st.execute();
    }

    @Override
    public void delete(int id) throws SQLException {
        String delete = "delete from profiles where id = ?;";
        PreparedStatement st = conn.prepareStatement(delete);
        st.setInt(1, id);
        st.execute();
    }

    public int allProfilesQuantity() throws SQLException {
        String count = "select count(*) from profiles;";
        PreparedStatement st = conn.prepareStatement(count);
        ResultSet rs = st.executeQuery();
        int quantity = 0;
        if (rs.next()){
            quantity = rs.getInt("count");
        }
        return quantity;
    }
}