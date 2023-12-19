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

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> cookieValue = CookiesService.getCookieValue(req);

        if (cookieValue.isEmpty() ||
                loginedUsersService.getLoginedUserIdByCookie(cookieValue.get()).isEmpty()) {
            resp.sendRedirect("/login");
            return;
        }

        int maxProfileId;
        int userId = loginedUsersService.getLoginedUserIdByCookie(cookieValue.get()).get();
        try {
            maxProfileId = userChoicesService.getSeenProfilesCount(userId).get();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (maxProfileId==profilesQuantity){
            resp.sendRedirect("liked");
            return;
        }

//        Потрібно детектити на якому профілі користувач зупинився,
//        щоб потім показувати останній профіль, який юзер ще не лайкнув чи
//        не дізлайкнув. Це можна зробити, присвоївши currentIndex-у кількість
//        переглянутих профілей
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
        try (PrintWriter writer = resp.getWriter()) {
            Template temp = cfg.getTemplate("like-dislike-page.html");
            temp.process(dataForFreemarker, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String choice = req.getParameter("choice");

        if (choice != null) {
            // получить id профиля и пользователя из значения кнопки
            String[] parts = choice.split("_");
            int viewedProfileId = Integer.parseInt(parts[0]);
            String userChoice = parts[1];

            String cookie = CookiesService.getCookieValue(req).get();

            //сохраненяем выбор в базу данных
            try {
                int loginedUserId = loginedUsersService.getLoginedUserIdByCookie(cookie).get();
                Like like = new Like(loginedUserId, viewedProfileId, userChoice);
                userChoicesService.add(like);

                //индекс текущего профиля, показывает по кругу
                currentIndex = (currentIndex + 1) % profilesService.getAll().size();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //если все профили уже просмотрены, переправляем на страницу /liked
            if (currentIndex == 0) {
                resp.sendRedirect("/liked");
                return;
            }

            resp.sendRedirect("/users");
        }
    }
}