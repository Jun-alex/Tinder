package app.servlet;

import app.model.CookiesService;
import app.model.Like;
import app.model.Profile;
import app.service.LoginedUsersService;
import app.service.ProfilesService;
import app.service.UserChoicesService;
import app.utility.ResourcesOps;
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

public class ProfilesServlet extends HttpServlet {

    private final LoginedUsersService loginedUsersService;
    private final UserChoicesService userChoicesService;
    private final ProfilesService profilesService;
    private final int profilesQuantity;

    private int currentIndex = 1;

    public ProfilesServlet(Connection conn) throws SQLException {
        loginedUsersService = new LoginedUsersService(conn);
        userChoicesService = new UserChoicesService(conn);
        profilesService = new ProfilesService(conn);
        profilesQuantity = profilesService.allProfilesQuantity();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> cookieValue = CookiesService.getCookieValue(request);
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
        Profile profile;
        try {
            profile = profilesService.getById(currentIndex).get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        dataForFreemarker.put("profile", profile);

        // шаблон FreeMarker
        try (PrintWriter writer = response.getWriter()) {
            Template temp = cfg.getTemplate("like-dislike-page.html");
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

            String cookie = CookiesService.getCookieValue(request).get();

            //сохраненяем выбор в базу данных
            try {
                int loginedUserId = loginedUsersService.getLoginedUserId(cookie);
                Like like = new Like(loginedUserId, viewedProfileId, userChoice);
                userChoicesService.add(like);

                //индекс текущего профиля, показывает по кругу
                currentIndex = (currentIndex + 1) % profilesService.getAll().size();
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