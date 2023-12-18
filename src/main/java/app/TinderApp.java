package app;

import app.db.Database;
import app.servlet.LikedProfilesServlet;
import app.servlet.LoginServlet;
import app.servlet.MessagesServlet;
import app.servlet.ProfilesServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.sql.Connection;
import java.sql.SQLException;


public class TinderApp {
    public static void main(String[] args) throws SQLException {
        int port = 8080;

//        Під'єднуємося до БД по відповідному URL, user, password
//        та створюємо змінну conn типу Connection для роботи з БД
        Connection conn = Database.mkConn();

        Server server = new Server(port);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        contextHandler.addServlet(new ServletHolder(new ProfilesServlet(conn)), "/users");
        contextHandler.addServlet(new ServletHolder(new LikedProfilesServlet(conn)), "/liked");
        contextHandler.addServlet(new ServletHolder(new MessagesServlet(conn)), "/messages/*");
        contextHandler.addServlet(new ServletHolder(new LoginServlet(conn)), "/login");

        contextHandler.addServlet(new ServletHolder(new LoginServlet(conn)), "/*");

        server.setHandler(contextHandler);

        try {
            server.start();
            System.out.println("Server started on http://localhost:" + port);
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }
}