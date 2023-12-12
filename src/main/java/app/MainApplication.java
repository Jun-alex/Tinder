package app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.sql.SQLException;


public class MainApplication {
    public static void main(String[] args) throws SQLException {
        int port = 8080;

        Server server = new Server(port);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        //hello путь
        contextHandler.addServlet(new ServletHolder(new HelloWorldServlet()), "/hello");

        //users путь
        contextHandler.addServlet(new ServletHolder(new UsersServlet()), "/users");

        //liked путь
        contextHandler.addServlet(new ServletHolder(new LikedProfilesServlet()), "/liked");


        contextHandler.addServlet(DefaultServlet.class, "/");

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
