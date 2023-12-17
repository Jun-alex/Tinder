package app.servlet;
import app.dao.UserChoicesSQL;
import app.model.Auth;
import app.utility.ResourcesOps;
import app.model.User;
import app.db.Database;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class LikedProfilesServlet extends HttpServlet {

    private final Connection conn;
    private final UserChoicesSQL userChoicesService;

    public LikedProfilesServlet() throws SQLException {
        conn = Database.mkConn();
        userChoicesService = new UserChoicesSQL();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> cookieValue = Auth.getCookieValue(request);
        if (cookieValue.isEmpty()) {
            response.sendRedirect("/login");
            return;
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));

        List<User> likedProfiles;
        try {
            likedProfiles = userChoicesService.getLikedProfiles();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> input = new HashMap<>();
        input.put("likedProfiles", likedProfiles);

        try (PrintWriter writer = response.getWriter()) {
            Template temp = cfg.getTemplate("people-list.html");
//            Показуємо користувачу html сторінку фрімаркеру (пролайкані профілі)
            temp.process(input, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }
}
