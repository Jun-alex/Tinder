package app.dao;

import app.model.Like;
import app.model.Profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserChoicesDAO implements DAO<Like>{
    private final Connection conn;

    public UserChoicesDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Like> getAll() throws SQLException {
        String selectAll = "select user_id, profile_id, choice from user_choices;";
        PreparedStatement st = conn.prepareStatement(selectAll);
        ResultSet rs = st.executeQuery();

        ArrayList<Like> data = new ArrayList<>();
        while (rs.next()) {
            Like like = Like.getLikeFromRs(rs);
            data.add(like);
        }
        return data;
    }

    @Override
    public Optional<Like> getById(int id) throws SQLException {
        String select = "select id, user_id, profile_id, choice from user_choices where id = ?;";
        PreparedStatement st = conn.prepareStatement(select);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if(rs.next()) {
            Like s = Like.getLikeFromRs(rs);
            return Optional.of(s);
        }
        return Optional.empty();
    }

    @Override
    public void add(Like like) throws SQLException {
        String insert = "INSERT INTO user_choices (user_id, profile_id, choice) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setInt(1, like.userId());
        ps.setInt(2, like.profileId());
        ps.setString(3, like.choice());
        ps.execute();
    }

    @Override
    public void update(Like like) throws SQLException {
        String update = """
                update user_choices
                set choice = ?,
                where user_id=? and profile_id=?;
                """;

        PreparedStatement st = conn.prepareStatement(update);
        st.setString(1, like.choice());
        st.setInt(2, like.userId());
        st.setInt(3, like.profileId());
        st.execute();
    }

    @Override
    public void delete(int id) throws SQLException {
        String delete = "delete from user_choices where id=?;";
        PreparedStatement st = conn.prepareStatement(delete);
        st.setInt(1, id);
        st.execute();
    }
    public Optional<Integer> getMaxProfileId() throws SQLException {
        String select = "select max(profile_id) from user_choices;";
        PreparedStatement ps = conn.prepareStatement(select);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            int max = rs.getInt("max");
            return Optional.of(max);
        }
        return Optional.of(0);
    }
    public List<Profile> getLikedProfiles() throws SQLException {
        String select = """
                  select p.id, name, photo_url from profiles p, user_choices uc
                  where uc.profile_id = p.id and uc.choice = 'like'
                  """;
        PreparedStatement ps = conn.prepareStatement(select);
        ResultSet rs = ps.executeQuery();

        ArrayList<Profile> likedProfiles = new ArrayList<>();
        while (rs.next()) {
            Profile profile = Profile.getUserFromRs(rs);
            likedProfiles.add(profile);
        }
        return likedProfiles;
    }
}