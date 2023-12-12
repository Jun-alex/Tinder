package app;

import java.net.URISyntaxException;

public class ResourcesOps {
    public static String dirUnsafe(String prefix) {
        try {
            return
                    ResourcesOps.class
                            .getClassLoader()
                            .getResource(prefix)
                            .toURI()
                            .getPath().substring(3); // Видаляємо зі стрінги /C: на початку, щоб не вилізла помилка;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
