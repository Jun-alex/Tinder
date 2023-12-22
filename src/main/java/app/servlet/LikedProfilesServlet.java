package app.servlet;

import app.model.CookiesService;
import app.service.LoginedUsersService;
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
    private final LoginedUsersService loginedUsersService;

    public LikedProfilesServlet(Connection conn) {
        userChoicesService = new UserChoicesService(conn);
        loginedUsersService = new LoginedUsersService(conn);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> cookieValue = CookiesService.getCookieValue(req);

        if (cookieValue.isEmpty() ||
                loginedUsersService.getLoginedUserIdByCookie(cookieValue.get()).isEmpty()) {
            resp.sendRedirect("/login");
            return;
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
//        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));
        cfg.setClassForTemplateLoading(LikedProfilesServlet.class, "/templates");


        List<Profile> likedProfiles;
        int userId = loginedUsersService.getLoginedUserIdByCookie(cookieValue.get()).get();
        try {
            likedProfiles = userChoicesService.getLikedProfiles(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> input = new HashMap<>();
        input.put("likedProfiles", likedProfiles);

        try (PrintWriter writer = resp.getWriter()) {
            Template temp = cfg.getTemplate("liked-people-list.html");
//            Показуємо користувачу html сторінку фрімаркеру (пролайкані профілі)
            temp.process(input, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }
}