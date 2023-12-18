package app.servlet;

import app.model.CookiesService;
import app.service.UserChoicesService;
import app.utility.ResourcesOps;
import app.model.Profile;
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

    private final UserChoicesService userChoicesService;

    public LikedProfilesServlet(Connection conn) {
        userChoicesService = new UserChoicesService(conn);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> cookieValue = CookiesService.getCookieValue(request);
        if (cookieValue.isEmpty()) {
            response.sendRedirect("/login");
            return;
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));

        List<Profile> likedProfiles;
        try {
            likedProfiles = userChoicesService.getLikedProfiles();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> input = new HashMap<>();
        input.put("likedProfiles", likedProfiles);

        try (PrintWriter writer = response.getWriter()) {
            Template temp = cfg.getTemplate("liked-people-list.html");
//            Показуємо користувачу html сторінку фрімаркеру (пролайкані профілі)
            temp.process(input, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }
}