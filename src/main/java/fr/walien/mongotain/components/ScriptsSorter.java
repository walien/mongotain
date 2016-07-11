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
        String[] vals1 = str1.split(SEPARATOR_PATTERN);
        String[] vals2 = str2.split(SEPARATOR_PATTERN);
        int i = 0;
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        return Integer.signum(vals1.length - vals2.length);
    }
}
