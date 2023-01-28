package ai.prime.knowledge.data;

import java.util.LinkedList;
import java.util.List;

public class NamingUtil {
    public static final String SEPARATOR = " ";
    public static final String TYPE_SEPARATOR = ":";
    public static final String LIST_SEPARATOR = ",";
    public static final String START_COMPLEX_EXPRESSION = "(";
    public static final String END_COMPLEX_EXPRESSION = ")";

    public static String[] separate(String value, String separator) {
        String parsed = value;

        while (parsed.indexOf(SEPARATOR + SEPARATOR)>0) {
            parsed = parsed.replaceAll(SEPARATOR + SEPARATOR, SEPARATOR);
        }

        parsed = cleanCompound(parsed);

        List<String> parts = new LinkedList<>();
        while (parsed.length() > 0) {
            String part = getFirstPart(parsed, separator);
            parsed = getRemaining(parsed, part, separator);
            parts.add(cleanCompound(part));
        }

        String[] partsArray = new String[parts.size()];
        parts.toArray(partsArray);

        return partsArray;
    }

    public static String cleanCompound(String value){
        if (value.startsWith(START_COMPLEX_EXPRESSION) &&
                value.endsWith(END_COMPLEX_EXPRESSION)) {
            int count=1;
            int i=1;
            while (count>0 && i<value.length()) {
                String c = value.substring(i, i+1);
                if (c.equals(START_COMPLEX_EXPRESSION)) {
                    count++;
                }

                if (c.equals(END_COMPLEX_EXPRESSION)) {
                    count--;
                }

                i++;

                if (count==0 && i<value.length()) { //The first and last compound are not linked
                    return value;
                }
            }

            value = value.substring(1, value.length() - 1);

            return cleanCompound(value);
        }

        return value;
    }

    private static String getFirstPart(String value, String separator) {
        int index = separatorIndex(value, separator);

        return value.substring(0, index);
    }

    private static int separatorIndex(String value, String separator) {
        int i = 0;
        int length = value.length();
        String part = value;

        while (i < length && !part.startsWith(separator)) {
            if (part.startsWith(START_COMPLEX_EXPRESSION)) {
                int count=1;
                i++;
                while (count>0 && i<value.length()) {
                    String c = value.substring(i, i + 1);

                    if (c.equals(START_COMPLEX_EXPRESSION)) {
                        count++;
                    }

                    if (c.equals(END_COMPLEX_EXPRESSION)) {
                        count--;
                    }

                    i++;
                }
            } else {
                i++;
            }

            part = value.substring(i);
        }

        return i;
    }

    private static String getRemaining(String original, String part, String separator) {
        var value = original.substring(part.length());
        if (value.startsWith(separator)) {
            value = value.substring(separator.length());
        }

        return value;
    }
}
