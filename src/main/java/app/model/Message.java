package app.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record Message(int userIdFrom, int userIdTo, String message, ZonedDateTime time) {
    public static Message getMessageFromRs(ResultSet rs) throws SQLException {
        int userIdFrom = rs.getInt("user_id_from");
        int userIdTo = rs.getInt("user_id");
        String message = rs.getString("message");
        Timestamp timestamp = rs.getTimestamp("time");
        ZonedDateTime time = timestamp.toInstant().atZone(ZoneId.systemDefault());
        return new Message(userIdFrom, userIdTo, message, time);
    }
    public String getTimeString() {
//        Метод, що використовується у фрімаркері чату та викликається
//        на кожному message та повертає текст у форматі "Dec 17, 8:16 PM"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a", Locale.ENGLISH);
        return formatter.format(time);
    }
}