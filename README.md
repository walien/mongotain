# Mongotain

A DBMaintain like but applied to the mongo NoSQL DB !

### How To

The artifact is not published on any repository yet so you'll have to build the artifact locally first :
```
mvn clean install
```

Import the JAR into your maven project :
 ```
    <dependency>
        <groupId>org.mongotain</groupId>
        <artifactId>mongotain-java</artifactId>
        <version>1.0-SNAPSHOT</version>
     </dependency>
 ```

Launch Mongotain during your app startup :
 ```
    Mongotain mongotain = Mongotain.builder()
                 .configDB(Optional.empty(), "mongotain_config") // MongoURI is set by default : mongodb://localhost:27017
                 .targetDB(Optional.empty(), "mongotain_target") // MongoURI is set by default : mongodb://localhost:27017
                 .scriptsPath(Paths.get("[PATH TO YOUR MONGODB JS SCRIPTS]"))
                 .build();

    mongotain.init();
 ```
