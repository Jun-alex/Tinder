package app;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/user_choices";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "mysecretpassword";

    //private List<Profile> profiles;
    //private DAO<Profile> profileDAO;
    private ProfileDAO profileDAO;

    private int currentIndex = 0;

    public UsersServlet() {
        //примеры профилей храним profileDAO
        profileDAO = new ProfilesInMemory();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(MainApplication.class, "templates");

        //передача в шаблон
        Map<String, Object> input = new HashMap<>();
        input.put("profiles", profileDAO);
        input.put("currentIndex", currentIndex);

        //шаблон FreeMarker
        StringWriter writer = new StringWriter();
        try {
            Template template = new Template("template", new StringReader(Templates.USERS_PAGE), cfg);
            template.process(input, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
            return;
        }

        // Вывод результата
        response.setContentType("text/html");
        response.getWriter().println(writer.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String choice = request.getParameter("choice");

        if (choice != null) {
            //получить ид профиля и пользователя из значения кнопки
            String[] parts = choice.split("_");
            int profileId = Integer.parseInt(parts[0]);
            String userChoice = parts[1];

            //сохраненяем выбор в базу данных
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO choices (profile_id, choice) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, profileId);
                    preparedStatement.setString(2, userChoice);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
                return;
            }

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
        } else {
            //сообщения об ошибке, если параметр "choice" не передан
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Choice parameter is missing");
        }
    }
}

