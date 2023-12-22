package app.servlet;

import app.model.CookiesService;
import app.model.LoginedUser;
import app.model.UserSession;
import app.service.LoginedUsersService;
import app.service.UserSessionsService;
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
    private final UserSessionsService userSessionsService;

    public LoginServlet(Connection conn) {
        this.loginedUsersService = new LoginedUsersService(conn);
        this.userSessionsService = new UserSessionsService(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> cookieValue = CookiesService.getCookieValue(req);

        if ( cookieValue.isPresent() &&
             loginedUsersService.getLoginedUserIdByCookie(cookieValue.get()).isPresent() ) {
            resp.sendRedirect("users");
            return;
        }
        else {
//        Маркаємо користувача, вписуємо значення в кукі
            CookiesService.setCookieValue(UUID.randomUUID().toString(), resp);
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
//        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));
        cfg.setClassForTemplateLoading(LoginServlet.class, "/templates");
//        Показуємо користувачу html сторінку форми логіну. При цьому передавати
//        фрімаркеру в HashMap нічого не потрібно
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
        String cookie = CookiesService.getCookieValue(req).orElse(UUID.randomUUID().toString());
        UserSession userSession = new UserSession(email, password, cookie);

//        Перевіряємо, чи в БД вже є email та password, введені користувачем
        if(loginedUsersService.loginIsPresent(email, password)) {
//        Якщо в БД вже є такий логін та пароль, то ми оновлюємо йому дані по кукі
            loginedUsersService.updateCookie(cookie, email, password);
            resp.sendRedirect("users");
            return;
        }

        try {
//        Зберігаємо інфу про кукі в БД
            userSessionsService.add(userSession);
//        Зберігаємо нового користувача в БД
            loginedUsersService.add(new LoginedUser(email, password, cookie));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resp.sendRedirect("users");
    }
}