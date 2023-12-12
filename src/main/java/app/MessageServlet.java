package app;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.eclipse.jetty.servlet.Source;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(ResourcesOps.dirUnsafe("templates")));

        Map<String, Object> input = new HashMap<>();
//        input.put("userName", "Hello MESSAGES!!!");

        try (PrintWriter w = resp.getWriter()) {
            String pathInfo = req.getPathInfo().substring(1);
            int chatId = 0;
            try {
                chatId = Integer.parseInt(pathInfo);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            System.out.printf("Chat id = %d", chatId);
            Template temp = cfg.getTemplate("chat.html");
            temp.process(input, w);
        }
        catch (TemplateException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
        }
    }
}

