package app;

import app.servlet.LikedProfilesServlet;
import app.servlet.LoginServlet;
import app.servlet.MessageServlet;
import app.servlet.UsersServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.sql.SQLException;


public class TinderApp {
    public static void main(String[] args) throws SQLException {
        int port = 8080;

        Server server = new Server(port);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        contextHandler.addServlet(new ServletHolder(new UsersServlet()), "/users");
        contextHandler.addServlet(new ServletHolder(new LikedProfilesServlet()), "/liked");
        contextHandler.addServlet(new ServletHolder(new MessageServlet()), "/message/*");
        contextHandler.addServlet(new ServletHolder(new LoginServlet()), "/login");

        contextHandler.addServlet(new ServletHolder(new LoginServlet()), "/*");

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
