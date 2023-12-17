package app.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Message {
    private int userIdFrom;
    private int userIdTo;
    private String message;
    private ZonedDateTime time;

    public Message(int userIdFrom, int userIdTo, String message, ZonedDateTime time) {
        this.userIdFrom = userIdFrom;
        this.userIdTo = userIdTo;
        this.message = message;
        this.time = time;
    }

    public int getUserIdFrom() {
        return userIdFrom;
    }
    public int getUserIdTo() {
        return userIdTo;
    }

    public String getMessage() {
        return message;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public String getTimeString() {
//        Метод, що використовується у фрімаркері чату та викликається
//        на кожному message та повертає текст у форматі "Dec 17, 8:16 PM"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a", Locale.ENGLISH);
        return formatter.format(time);
    }
}
