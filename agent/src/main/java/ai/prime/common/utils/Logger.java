package ai.prime.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class Logger {
    private static final String INFO = "info";
    private static final String DEBUG = "debug";
    private static HashSet<String> keysToLog;

    //TODO support refreshing at runtime
    private static void load() {
        var inputStream = Logger.class.getResourceAsStream("/logKeys.txt");
        var inputStreamReader = new InputStreamReader(inputStream);
        try (BufferedReader br = new BufferedReader(inputStreamReader)) {
            keysToLog = new HashSet<>();
            String key;
            while ((key = br.readLine()) != null) {
                keysToLog.add(key);
            }
        } catch (IOException e) {
            System.err.println("Error reading keys from file: " + e.getMessage());
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

    private static String getFormattedMessage(String key, String message) {
        return getTime() + "\t" + key + ":\t" + message;
    }

    private static void print(String key, String message, String type) {
        if (keysToLog.contains(key) && keysToLog.contains(type)) {
            System.out.println(getFormattedMessage(key, message));
        }
    }

    public static void info(String key, String message) {
        print(key, message, INFO);
    }

    public static void debug(String key, String message) {
        print(key, message, DEBUG);
    }

    public static void error(String message) {
        System.err.println(getFormattedMessage("ERROR!!!", message));
    }

    public static void error(String message, Exception exception) {
        error(message);
        System.err.println(exception);
    }
}


