package app;

import app.dao.ProfilesInMemory;
import app.db.Database;
import app.httpfilter.LoginFilter;
import app.model.Profile;
import app.servlet.LikedProfilesServlet;
import app.servlet.LoginServlet;
import app.servlet.MessagesServlet;
import app.servlet.ProfilesServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;


public class TinderApp {
    public static void main(String[] args) throws SQLException {
        int port = 8080;

//        Під'єднуємося до БД по відповідному URL, user, password
//        та створюємо змінну conn типу Connection для роботи з БД
        Connection conn = Database.mkConn();

        Server server = new Server(port);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        EnumSet<DispatcherType> tpe = EnumSet.of(DispatcherType.REQUEST);

        contextHandler.addServlet(new ServletHolder(new ProfilesServlet(conn)), "/users");
        contextHandler.addFilter(LoginFilter.class, "/users", tpe);

        contextHandler.addServlet(new ServletHolder(new LikedProfilesServlet(conn)), "/liked");
        contextHandler.addFilter(LoginFilter.class, "/liked", tpe);

        contextHandler.addServlet(new ServletHolder(new MessagesServlet(conn)), "/messages/*");
        contextHandler.addFilter(LoginFilter.class, "/messages", tpe);


        contextHandler.addServlet(new ServletHolder(new LoginServlet(conn)), "/login");

        contextHandler.addServlet(new ServletHolder(new LoginServlet(conn)), "/*");

        server.setHandler(contextHandler);

        try {
            server.start();
//            System.out.println("Server started on http://localhost:" + port);
            System.out.println("Server started on ec2-18-196-235-59.eu-central-1.compute.amazonaws.com");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }
}