package app.servlet;

import app.model.CookiesService;
import app.model.Message;
import app.model.Profile;
import app.service.LoginedUsersService;
import app.service.MessagesService;
import app.service.ProfilesService;
import app.service.UserChoicesService;
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
import java.time.ZonedDateTime;
import java.util.*;

public class MessagesServlet extends HttpServlet {
    private final ProfilesService profilesService;
    private final LoginedUsersService loginedUsersService;
    private final MessagesService messagesService;
    private final UserChoicesService userChoicesService;

    public MessagesServlet(Connection conn) {
        profilesService = new ProfilesService(conn);
        loginedUsersService = new LoginedUsersService(conn);
        messagesService = new MessagesService(conn);
        userChoicesService = new UserChoicesService(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> cookieValue = CookiesService.getCookieValue(req);

        if (cookieValue.isEmpty() ||
                loginedUsersService.getLoginedUserIdByCookie(cookieValue.get()).isEmpty()) {
            resp.sendRedirect("/login");
            return;
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
//        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));
        cfg.setClassForTemplateLoading(MessagesServlet.class, "/templates");

//        Отримуємо loginedUserId(user_id_from) (id людини, яка надсилає повідомлення)
        int loginedUserId = loginedUsersService.getLoginedUserIdByCookie(cookieValue.get()).get();

        int chatId = 0;
//        Отримуємо chatId(user_id_to) (id людини, якій надсилаємо повідомлення) з параметра URL
        String pathInfo = req.getPathInfo().substring(1);

        try {
            chatId = Integer.parseInt(pathInfo);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

//        Якщо користувач зайде по ендпоінту "/messages/${id}", перевіряємо
//        чи користувач лайкав відповідний профіль. Якщо не лайкав, то пишемо, що
//        сторінка не знайдена
        if( !userChoicesService.checkWhetherProfileWasLiked(loginedUserId, chatId) ) {
            resp.sendError(404, "Sorry, you have no access to text to the user. You have not liked this profile!");
            return;
        }

        Profile profile;
        List<Message> messagesForParticularChat;
        try {
            profile = profilesService.getById(chatId).get();
            messagesForParticularChat = messagesService.getMessagesForParticularChat(loginedUserId, chatId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> input = new HashMap<>();
        input.put("profile", profile);
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
        String cookie = CookiesService.getCookieValue(req).get();

        ZonedDateTime time = ZonedDateTime.now(); // 2023-12-14T13:35:13.964446900+02:00[Europe/Kiev] format
        String path = req.getPathInfo();
        int userIdTo = Integer.parseInt(path.substring(1));
        String input = req.getParameter("input"); // Записує в змінну те, що ввів користувач в input

        try {
            int loginedUserId = loginedUsersService.getLoginedUserIdByCookie(cookie).get();
            // Вписуємо в базу даних повідомлення від користувача
            messagesService.add(new Message(loginedUserId, userIdTo, input, time));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        doGet(req,resp);
    }
}