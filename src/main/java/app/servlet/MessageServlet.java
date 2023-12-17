package app.servlet;

import app.dao.LoginedUsersSQL;
import app.dao.MessageSQL;
import app.model.Auth;
import app.model.Message;
import app.utility.ResourcesOps;
import app.model.User;
import app.dao.UsersSQL;
import app.dao.DAO;
import app.db.Database;
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
import java.time.ZonedDateTime;
import java.util.*;

public class MessageServlet extends HttpServlet {
    private final Connection conn;
    private final DAO<User> usersDatabase;
    private final LoginedUsersSQL loginedUsersService;
    private final MessageSQL messagesDatabase;

    public MessageServlet() throws SQLException {
        conn = Database.mkConn();
        usersDatabase = new UsersSQL();
        loginedUsersService = new LoginedUsersSQL();
        messagesDatabase = new MessageSQL();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> cookieValue = Auth.getCookieValue(req);
        if (cookieValue.isEmpty()) {
            resp.sendRedirect("/login");
            return;
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));

//        Отримуємо user_id_from (id людини, яка надсилає повідомлення)
        String cookie = Auth.getCookieValue(req).get();
        int loginedUserId;
        try {
            loginedUserId = loginedUsersService.getLoginedUserId(cookie);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int chatId = 0;
//        Отримуємо user_id_to (id людини, якій надсилаємо повідомлення) з параметра URL
        String pathInfo = req.getPathInfo().substring(1);
        try {
            chatId = Integer.parseInt(pathInfo);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        User user;
        List<Message> messagesForParticularChat;
        try {
            user = usersDatabase.getById(chatId).get();
            messagesForParticularChat = messagesDatabase.getMessagesForParticularChat(loginedUserId, chatId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> input = new HashMap<>();
        input.put("user", user);
        input.put("messages", messagesForParticularChat);
        input.put("chatId", chatId);

        try (PrintWriter w = resp.getWriter()) {
            Template temp = cfg.getTemplate("chat.html");
            temp.process(input, w);
        }
        catch (TemplateException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cookie = Auth.getCookieValue(req).get();

        ZonedDateTime time = ZonedDateTime.now(); // 2023-12-14T13:35:13.964446900+02:00[Europe/Kiev] format
        String path = req.getPathInfo();
        int userIdTo = Integer.parseInt(path.substring(1));
        String input = req.getParameter("input"); // Записує в змінну те, що ввів користувач в input

        try {
            int loginedUserId = loginedUsersService.getLoginedUserId(cookie);
            // Вписуємо в базу даних повідомлення від користувача
            messagesDatabase.add(new Message(loginedUserId, userIdTo, input, time));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        doGet(req,resp);
    }
}

