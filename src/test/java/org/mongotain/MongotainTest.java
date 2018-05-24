package org.mongotain;

import org.junit.Test;

import java.nio.file.Paths;

public class MongotainTest {

    @Test
    public void should_properly_start_mongotain() {

        Mongotain mongotain = Mongotain.builder()
                .configDB("mongotain_config")
                .targetDB("mongotain_target")
                .scriptsPath(Paths.get("/Users/eoriou/dev/tests/mongotain"))
                .build();

        mongotain.start();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_properly_crash_because_of_missing_param() {

        Mongotain mongotain = Mongotain.builder()
                .configDB(null)
                .targetDB("mongotain_target")
                .scriptsPath(Paths.get("/Users/eoriou/dev/tests/mongotain"))
                .build();

        mongotain.start();
    }
}