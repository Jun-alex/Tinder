package app.model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

public class CookiesService {
    private final static String cookieName = "UID";
    private final static Cookie[] EMPTY = new Cookie[0];
    private static Cookie[] checkNull(Cookie[] cookies) {
        return cookies != null ? cookies : EMPTY;
    }
    public static Optional<Cookie> getCookie(HttpServletRequest req) {
        Cookie[] cookies = checkNull(req.getCookies()); // can be null
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(cookieName))
                .findFirst();
    }
    public static Optional<String> getCookieValue(HttpServletRequest req) {
        Optional<Cookie> cookie = getCookie(req);
        return cookie.map(Cookie::getValue);
    }
    public static void setCookie(Cookie cookie, HttpServletResponse resp) {
        resp.addCookie(cookie);
    }
    public static void setCookieValue(String cookieValue, HttpServletResponse resp) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(365 * 24 * 60 * 60); // Зберігаємо кукі рік з моменту присвоєння
        resp.addCookie(cookie);
    }
    public static void clearCookie(HttpServletResponse resp) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }
}