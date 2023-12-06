package app;

public class Templates {
    //главная страница
    public static final String USERS_PAGE = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Users Page</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Users Page</h1>\n" +
            //   "\n" +
            "<form action=\"/users\" method=\"post\">\n" +
            "    <#assign currentProfile = profiles.get(currentIndex)>\n" +
            "    <input type=\"hidden\" name=\"currentIndex\" value=\"${currentIndex}\">\n" +
            "    <div>\n" +
            "        <h2>${currentProfile.name}</h2>\n" +
            "        <img src=\"${currentProfile.photoUrl}\" alt=\"${currentProfile.name}\" style=\"max-width: 200px;\">\n" +
            "        <button type=\"submit\" name=\"choice\" value=\"${currentProfile.id}_yes\">Yes</button>\n" +
            "        <button type=\"submit\" name=\"choice\" value=\"${currentProfile.id}_no\">No</button>\n" +
            "    </div>\n" +
            "</form>\n" +
            //  "\n" +
            "</body>\n" +
            "</html>";

    //лайкнутые профили
    public static final String LIKED_PROFILES_PAGE = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Liked Profiles</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Liked Profiles</h1>\n" +
            "<ul>\n" +
            "    <#list likedProfiles as profile>\n" +
            "        <li>\n" +
            "            <h2>${profile.name}</h2>\n" +
            "            <img src=\"${profile.photoUrl}\" alt=\"${profile.name}\" style=\"max-width: 200px;\">\n" +
            "        </li>\n" +
            "    </#list>\n" +
            "</ul>\n" +
            "</body>\n" +
            "</html>";
}

//<#list profiles.getAll() as profile>\n" + </#list>\n" +
