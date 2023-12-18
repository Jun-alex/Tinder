package app.servlet;

import app.model.CookiesService;
import app.model.LoginedUser;
import app.service.LoginedUsersService;
import app.utility.ResourcesOps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class LoginServlet extends HttpServlet {
    private final LoginedUsersService loginedUsersService;

    public LoginServlet(Connection conn) {
        this.loginedUsersService = new LoginedUsersService(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> cookieValue = CookiesService.getCookieValue(req);
        if (cookieValue.isPresent()) {
            resp.sendRedirect("users");
        }
        else {
//        Маркаємо користувача, вписуємо значення в кукі
            CookiesService.setCookieValue(UUID.randomUUID().toString(), resp);
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));

//        Показуємо користувачу html сторінку форми логіну. При цьому передавати
//        фрімаркеру в HashMap нічого не потірбно
        try (PrintWriter w = resp.getWriter()) {
            Template temp = cfg.getTemplate("login.html");
            temp.process(new HashMap<>(), w);
        }
        catch (TemplateException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String cookie = CookiesService.getCookieValue(req).get();
//        Зберігаємо всю інфу в БД
        try {
            loginedUsersService.add(new LoginedUser(email, password, cookie));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resp.sendRedirect("users");
    }
}