package fr.walien.mongotain.components;

import fr.walien.mongotain.domain.Script;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScriptsSorter {

    private static final String SEPARATOR = ".";
    private static final String SEPARATOR_PATTERN = "\\.";
    private static final Pattern IS_INTEGER = Pattern.compile("^\\d+$");

    public int sortByName(Script script1, Script script2) {
        return versionCompare(extractVersion(script1), extractVersion(script2));
    }

    private String extractVersion(Script script) {
        return Stream.of(script.getPath().getFileName().toString().split(SEPARATOR_PATTERN))
                .filter(elt -> IS_INTEGER.matcher(elt).matches())
                .collect(Collectors.joining(SEPARATOR));
    }

    public static int versionCompare(String str1, String str2) {
        String[] versions1 = str1.split(SEPARATOR_PATTERN);
        String[] versions2 = str2.split(SEPARATOR_PATTERN);
        int i = 0;
        while (i < versions1.length && i < versions2.length && versions1[i].equals(versions2[i])) {
            i++;
        }
        if (i < versions1.length && i < versions2.length) {
            int diff = Integer.valueOf(versions1[i]).compareTo(Integer.valueOf(versions2[i]));
            return Integer.signum(diff);
        }
        return Integer.signum(versions1.length - versions2.length);
    }
}
