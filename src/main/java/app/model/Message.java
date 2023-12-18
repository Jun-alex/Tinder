package app.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record Message(int userIdFrom, int userIdTo, String message, ZonedDateTime time) {

    public String getTimeString() {
//        Метод, що використовується у фрімаркері чату та викликається
//        на кожному message та повертає текст у форматі "Dec 17, 8:16 PM"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a", Locale.ENGLISH);
        return formatter.format(time);
    }
}