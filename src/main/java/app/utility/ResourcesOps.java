package app.utility;

import java.net.URISyntaxException;
import java.net.URL;

public class ResourcesOps {
//    public static String dirUnsafe(String prefix) {
//        try {
//            return
//                    ResourcesOps.class
//                            .getClassLoader()
//                            .getResource(prefix)
//                            .toURI()
//                            .getPath().substring(3); // Видаляємо зі стрінги /C: на початку, щоб не вилізла помилка;
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }
public static String dirUnsafe(String prefix) {
    try {
        return ResourcesOps.class.getClassLoader().getResource(prefix).toURI().getPath();
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    }
}
//public static String dirUnsafe(String prefix) {
//    URL resourceUrl = ResourcesOps.class.getClassLoader().getResource(prefix);
//    if (resourceUrl == null) {
//        throw new RuntimeException("Resource not found for prefix: " + prefix);
//    }
//    try {
//        return resourceUrl.toURI().getPath().substring(3);
//    } catch (URISyntaxException e) {
//        throw new RuntimeException(e);
//    }
//}

//    public static String dirUnsafe(String prefix) {
//        URL resourceUrl = ResourcesOps.class.getClassLoader().getResource(prefix);
//
//        if (resourceUrl == null) {
//            throw new RuntimeException("Resource not found for prefix: " + prefix);
//        }
//
//        try {
//            String path = resourceUrl.toURI().getPath();
//            if (path == null) {
//                throw new RuntimeException("Resource path is null for prefix: " + prefix);
//            }
//            return path.substring(3);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
