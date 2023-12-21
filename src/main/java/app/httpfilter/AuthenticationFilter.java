package app.httpfilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationFilter extends HttpFilter {
    @Override
    default void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    default void destroy() {
    }

    @Override
    default boolean myCheckLogic(HttpServletRequest request) {
        // Implement your authentication logic here
        return isAuthenticated(request);
    }

    @Override
    default void behaviorIfLogicNotPassed(HttpServletRequest request,
                                          HttpServletResponse response) throws IOException {
        // Redirect unauthenticated users to the login page
        response.sendRedirect("/login");
    }

    boolean isAuthenticated(HttpServletRequest request);
}
