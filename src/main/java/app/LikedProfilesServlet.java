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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikedProfilesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(MainApplication.class, "templates");

        //список понравившихся профилей
        List<Profile> likedProfiles = getLikedProfiles();

        //передаём в шаблон
        Map<String, Object> input = new HashMap<>();
        input.put("likedProfiles", likedProfiles);

        //шаблон FreeMarker
        StringWriter writer = new StringWriter();
        try {
            Template template = new Template("liked-template", new StringReader(Templates.LIKED_PROFILES_PAGE), cfg);
            template.process(input, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing error");
            return;
        }

        //вывод результата
        response.setContentType("text/html");
        response.getWriter().println(writer.toString());
    }

    private List<Profile> getLikedProfiles() {
        //профили которые понравились
        List<Profile> likedProfiles = new ArrayList<>();
        likedProfiles.add(new Profile(1, "John",
                "https://hips.hearstapps.com/hmg-prod/images/gettyimages-936360206.jpg"));
        likedProfiles.add(new Profile(2, "Jane",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQm48hud8Rmxfcr21GAjCeqJ2PfS-foEjPbcQ&usqp=CAU"));
        likedProfiles.add(new Profile(3, "Madison",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Madison_Beer_2019_by_Glenn_Francis_%28cropped%29.jpg/1200px-Madison_Beer_2019_by_Glenn_Francis_%28cropped%29.jpg"));
        return likedProfiles;
    }
}
