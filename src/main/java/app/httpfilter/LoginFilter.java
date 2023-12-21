package app.httpfilter;

import app.model.CookiesService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

public class LoginFilter implements AuthenticationFilter {
    @Override
    public boolean isAuthenticated(HttpServletRequest req) {
        // Implement your specific authentication logic here
//        Optional<String> userId = getUserIdFromSession(request);
//        return userId.isPresent();
        String cookie = CookiesService.getCookieValue(req).orElse(null);
        // Return true if the cookie is present, false otherwise
        return cookie != null;
    }

    // You need to implement this method based on how user information is stored in the session
    private Optional<String> getUserIdFromSession(HttpServletRequest request) {
        // Example: Retrieve user ID from session attribute
        return Optional.ofNullable((String) request.getSession().getAttribute("userId"));
    }
}
