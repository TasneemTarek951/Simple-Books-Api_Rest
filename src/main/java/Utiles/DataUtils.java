package Utiles;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class DataUtils {
    private static Properties props = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static void set(String key, String value) {
        props.setProperty(key,value);
        try (FileOutputStream fos = new FileOutputStream("src/main/resources/config.properties")) {
            props.store(fos, "Updated by DataUtils");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}