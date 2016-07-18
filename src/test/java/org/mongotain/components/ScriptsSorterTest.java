package org.mongotain.components;

import com.google.common.collect.ImmutableList;
import org.mongotain.domain.Script;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScriptsSorterTest {

    @Test
    public void should_properly_sort_scripts_by_name() {

        ScriptsSorter scriptsSorter = new ScriptsSorter();
        List<Script> sorted = ImmutableList.of(
                script("/path/to/test/9.2.test.js"),
                script("/path/to/test/8.5.2.test.js"),
                script("/path/to/test/1.test.js"),
                script("/path/to/test/1.6.test.js"),
                script("/path/to/test/3.test.js"),
                script("/path/to/test/3.22.6.test.js"),
                script("/path/to/test/3.22.5.test.js"),
                script("/path/to/test/2.test.js")
        )
                .stream()
                .sorted(scriptsSorter::sortByName)
                .collect(Collectors.toList());

        ImmutableList<Script> expects = ImmutableList.of(script("/path/to/test/1.test.js"),
                script("/path/to/test/1.6.test.js"),
                script("/path/to/test/2.test.js"),
                script("/path/to/test/3.test.js"),
                script("/path/to/test/3.22.5.test.js"),
                script("/path/to/test/3.22.6.test.js"),
                script("/path/to/test/8.5.2.test.js"),
                script("/path/to/test/9.2.test.js"));

        assert Objects.equals(sorted, expects);
    }

    private Script script(String scriptPath) {
        return new Script().setPath(Paths.get(scriptPath));
    }
}