package ai.prime.common.utils;

import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

public class Settings {
    private static final  String FILE = "/settings.properties";
    private static Properties settings;

    //TODO support refreshing at runtime
    private static void load() {
        settings = new Properties();

        try {
            settings.load(new InputStreamReader(Objects.requireNonNull(Settings.class.getResourceAsStream(FILE))));
        } catch(Exception e){
            Logger.error("Failed loading settings", e);
        }

        Logger.info("settings", "Finished loading settings");
    }

    static{
        load();
    }

    public static String getStringProperty(String key) {
        String value = settings.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Failed getting property for key " + key);
        }

        return value;
    }

    public static double getDoubleProperty(String key){
        String value = getStringProperty(key);
        return Double.valueOf(value).doubleValue();
    }

    public static int getIntProperty(String key){
        String value = getStringProperty(key);
        return Integer.valueOf(value).intValue();
    }

    public static boolean getBooleanProperty(String key){
        String value = getStringProperty(key);
        return Boolean.valueOf(value).booleanValue();
    }

}
