package app;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikedProfilesServlet extends HttpServlet {

    private final Connection conn;
    private final ProfilesInMemory profilesInMemory = new ProfilesInMemory();

    public LikedProfilesServlet() throws SQLException {
        conn = Database.mkConn();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));
//        cfg.setClassForTemplateLoading(MainApplication.class, "templates");

        //список понравившихся профилей
        List<Profile> likedProfiles = new ArrayList<>();
        try {
            likedProfiles = getLikedProfiles();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //передаём в шаблон
        Map<String, Object> input = new HashMap<>();
        input.put("likedProfiles", likedProfiles);

        //шаблон FreeMarker
//        StringWriter writer = new StringWriter();
        try (PrintWriter writer = response.getWriter()) {
//            Template template = new Template("liked-template", new StringReader(Templates.LIKED_PROFILES_PAGE), cfg);
            Template temp = cfg.getTemplate("people-list.html");
            temp.process(input, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }

    private List<Profile> getLikedProfiles() throws SQLException {
        String select = "select id, profile_id, choice from user_choices where choice='like';";
        PreparedStatement ps = conn.prepareStatement(select);
        ResultSet rs = ps.executeQuery();

        ArrayList<Profile> likedProfiles = new ArrayList<>();
        while (rs.next()) {
            int likedProfileId = rs.getInt("profile_id");
            Profile profile = profilesInMemory.getById(likedProfileId);
            likedProfiles.add(profile);
        }
        return likedProfiles;
    }
}
