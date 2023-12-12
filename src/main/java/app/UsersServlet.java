package app;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersServlet extends HttpServlet {

    private Connection conn;

    //private List<Profile> profiles;
    //private DAO<Profile> profileDAO;
    private ProfileDAO profileDAO;

    private int currentIndex = 0;

    public UsersServlet() throws SQLException {
        conn = Database.mkConn();
        //примеры профилей храним profileDAO
        profileDAO = new ProfilesInMemory();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
//        cfg.setClassForTemplateLoading(MainApplication.class, "templates");
        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));

        //передача в шаблон
        Map<String, Object> dataForFreemarker = new HashMap<>();
        dataForFreemarker.put("profiles", profileDAO);
        dataForFreemarker.put("currentIndex", currentIndex);

        //шаблон FreeMarker
//        StringWriter writer = new StringWriter();
        try (PrintWriter writer = response.getWriter()) {
//            Template template = new Template("template", new StringReader(Templates.USERS_PAGE), cfg);
            Template temp = cfg.getTemplate("like-page.html");
//            Template template = new Template("template", new StringReader(Templates.USERS_PAGE), cfg);
            temp.process(dataForFreemarker, writer);
//            template.process(input, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }

//        response.setContentType("text/html");
//        response.getWriter().println(writer.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String choice = request.getParameter("choice");

        if (choice != null) {
            //получить ид профиля и пользователя из значения кнопки
            String[] parts = choice.split("_");
            int viewedProfileId = Integer.parseInt(parts[0]);
            String userChoice = parts[1];

            //сохраненяем выбор в базу данных
            String query = "INSERT INTO user_choices (profile_id, choice) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, viewedProfileId);
                ps.setString(2, userChoice);
                ps.executeUpdate();

                //индекс текущего профиля, показывает по кругу
                currentIndex = (currentIndex + 1) % profileDAO.getAll().size();

                //если все профили уже просмотрены, переправляем на страницу /liked
                if (currentIndex == 0) {
                    response.sendRedirect("/liked");
                    return;
                }

                response.sendRedirect("/users");
                //response.getWriter().println("Choice for profile " +profileId+ ": " +userChoice);
                //System.out.println("Choice for profile " +profileId+ ": " +userChoice);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

