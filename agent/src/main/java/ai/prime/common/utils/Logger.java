package ai.prime.common.utils;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

public class Logger {
    private static final  String FILE = "/logKeys.properties";
    private static final String INFO = "info";
    private static final String DEBUG = "debug";
    private static final String ERROR = "error";

    private static Properties keysToLog;

    //TODO support refreshing at runtime
    private static void load() {
        keysToLog = new Properties();

        try {
            keysToLog.load(new InputStreamReader(Objects.requireNonNull(Logger.class.getResourceAsStream(FILE))));
        } catch(Exception e){
            Logger.error("Failed loading settings", e);
        }
    }

    static {
        load();
    }

    private static String getTime() {
        var date = new Date();
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return dateFormat.format(date);
    }

    private static String getFormattedMessage(String key, String message, String type) {
        return getTime() + "\t" + type.toUpperCase() + "\t" + key + ":\t" + message;
    }

    private static void print(String key, Supplier<String> messageGetter, String type) {
        if (!keysToLog.containsKey(key)) {
            return;
        }

        if (keysToLog.getProperty(key).equals(type) ||
                (type.equals(INFO) && keysToLog.getProperty(key).equals(DEBUG))) {
            System.out.println(getFormattedMessage(key, messageGetter.get(), type));
        }
    }

    public static void info(String key, Supplier<String> messageGetter) {
        print(key, messageGetter, INFO);
    }

    public static void debug(String key, Supplier<String> messageGetter) {
        print(key, messageGetter, DEBUG);
    }

    public static void error(String message) {
        System.err.println(getFormattedMessage("", message, ERROR));
    }

    public static void error(String message, Exception exception) {
        error(message);
        System.err.println(exception);
        exception.printStackTrace();
    }
}


