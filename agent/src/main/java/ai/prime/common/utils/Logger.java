package ai.prime.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class Logger {
    private static final String ALL = "all";
    private static HashSet<String> keysToLog;

    static {
        var inputStream = Logger.class.getResourceAsStream("/keys.txt");
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

    private static String getTime() {
        var date = new Date();
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return dateFormat.format(date);
    }

    private static String getFormattedMessage(String key, String message) {
        return getTime() + "\t" + key + ":\t" + message;
    }

    public static void log(String key, String message) {
        if (keysToLog.contains(key) || keysToLog.contains(ALL)) {
            System.out.println(getFormattedMessage(key, message));
        }
    }

    public static void error(String message) {
        System.err.println(getFormattedMessage("ERROR!!!", message));
    }

    public static void error(String message, Exception exception) {
        error(message);
        System.err.println(exception);
    }
}


