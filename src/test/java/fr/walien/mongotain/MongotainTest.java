package fr.walien.mongotain;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Optional;

public class MongotainTest {

    @Test
    public void should_properly_start_mongotain() {

        Mongotain mongotain = Mongotain.builder()
                .configDB(Optional.empty(), "mongotain_config")
                .targetDB(Optional.empty(), "mongotain_target")
                .scriptsPath(Paths.get("/Users/eoriou/dev/tests/mongotain"))
                .build();

        mongotain.init();
    }

}