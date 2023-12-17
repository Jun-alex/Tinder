package app.servlet;

import app.dao.LoginedUsersSQL;
import app.dao.UserChoicesSQL;
import app.dao.UsersSQL;
import app.model.Auth;
import app.model.Like;
import app.model.User;
import app.utility.ResourcesOps;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UsersServlet extends HttpServlet {

    private Connection conn;
    private LoginedUsersSQL loginedUsersService;
    private UserChoicesSQL userChoicesService;
    private UsersSQL profileDAO;
    private int profilesQuantity;

    private int currentIndex = 1;

    public UsersServlet() throws SQLException {
        conn = Database.mkConn();
        profileDAO = new UsersSQL();
        userChoicesService = new UserChoicesSQL();
        loginedUsersService = new LoginedUsersSQL();
        profilesQuantity = profileDAO.allProfilesQuantity();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> cookieValue = Auth.getCookieValue(request);
        if (cookieValue.isEmpty()) {
            response.sendRedirect("/login");
            return;
        }

        int maxProfileId;
        try {
            maxProfileId = userChoicesService.getMaxProfileId().get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (maxProfileId==profilesQuantity){
            response.sendRedirect("liked");
            return;
        }

//        Потрібно детектити на якому профілі користувач зупинився,
//        щоб потім показувати останній профіль, який юзер ще не лайкнув чи
//        не дізлайкнув. Це можна зробити, присвоївши currentIndex-у найбільший
//        id лайкнутого профіля (і додавши 1), оскільки профілі ми показуємо по порядку
        currentIndex = maxProfileId + 1; // maxProfileId може бути >= 0

        // FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));

        // передача в шаблон
        Map<String, Object> dataForFreemarker = new HashMap<>();
        User profile;
        try {
            profile = profileDAO.getById(currentIndex).get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        dataForFreemarker.put("profile", profile);

        // шаблон FreeMarker
        try (PrintWriter writer = response.getWriter()) {
            Template temp = cfg.getTemplate("like-page.html");
            temp.process(dataForFreemarker, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String choice = request.getParameter("choice");

        if (choice != null) {
            // получить id профиля и пользователя из значения кнопки
            String[] parts = choice.split("_");
            int viewedProfileId = Integer.parseInt(parts[0]);
            String userChoice = parts[1];

            String cookie = Auth.getCookieValue(request).get();

            //сохраненяем выбор в базу данных
            try {
                int loginedUserId = loginedUsersService.getLoginedUserId(cookie);
                Like like = new Like(loginedUserId, viewedProfileId, userChoice);
                userChoicesService.add(like);

                //индекс текущего профиля, показывает по кругу
                currentIndex = (currentIndex + 1) % profileDAO.getAll().size();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //если все профили уже просмотрены, переправляем на страницу /liked
            if (currentIndex == 0) {
                response.sendRedirect("/liked");
                return;
            }

            response.sendRedirect("/users");
        }
    }
}

