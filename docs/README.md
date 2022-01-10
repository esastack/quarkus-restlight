# Quarkus-restlight

`Quarkus-restlight` is an extension of `esa-restlight`. It can use the `quarkus` framework to help users compile `restlight` applications into `native image`, shorten the startup time to milliseconds, and greatly reduce the memory usage, while reducing the image size from 100M+ to 10M+.

## Features

Support all features supported by `esa-restlight`, see details：[esa-restlight](https://www.esastack.io/esa-restlight/)。

## Env

- Java 11+ installed with `JAVA_HOME` configured appropriately.
- Apache Maven 3.8.1+

## Quick Start

#### Step 1: Add dependency

```xml
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>${quarkus.platform.group-id}</groupId>
                <artifactId>${quarkus.platform.artifact-id}</artifactId>
                <version>${quarkus.platform.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>io.esastack</groupId>
            <artifactId>quarkus-restlight-springmvc</artifactId>
            <version>${quarkus-restlight.version}</version>
        </dependency>

        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-arc</artifactId>
        </dependency>

        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-netty</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>${quarkus.platform.group-id}</groupId>
                <artifactId>quarkus-maven-plugin</artifactId>
                <version>${quarkus.platform.version}</version>
                <extensions>true</extensions>
                <executions>
                    <execution>
                        <goals>
                            <goal>build</goal>
                            <goal>generate-code</goal>
                            <goal>generate-code-tests</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${compiler-plugin.version}</version>
                <configuration>
                    <parameters>${maven.compiler.parameters}</parameters>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${surefire-plugin.version}</version>
                <configuration>
                    <systemPropertyVariables>
                        <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
                        <maven.home>${maven.home}</maven.home>
                    </systemPropertyVariables>
                </configuration>
            </plugin>
        </plugins>
    </build>
    <profiles>
        <profile>
            <id>native</id>
            <activation>
                <property>
                    <name>native</name>
                </property>
            </activation>
            <build>
                <plugins>
                    <plugin>
                        <artifactId>maven-failsafe-plugin</artifactId>
                        <version>${surefire-plugin.version}</version>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>integration-test</goal>
                                    <goal>verify</goal>
                                </goals>
                                <configuration>
                                    <systemPropertyVariables>
                                        <native.image.path>
                                            ${project.build.directory}/${project.build.finalName}-runner
                                        </native.image.path>
                                        <java.util.logging.manager>org.jboss.logmanager.LogManager
                                        </java.util.logging.manager>
                                        <maven.home>${maven.home}</maven.home>
                                    </systemPropertyVariables>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>

            <properties>
                <quarkus.package.type>native</quarkus.package.type>
                <quarkus.native.debug.enabled>true</quarkus.native.debug.enabled>
                <quarkus.native.enable-dashboard-dump>true</quarkus.native.enable-dashboard-dump>
                <quarkus.native.enable-reports>true</quarkus.native.enable-reports>
            </properties>
        </profile>
    </profiles>

```

#### Step 2: Define  controller

```java
@RequestMapping("/hello/springmvc")
@RegisterForReflection
public class HelloController {

    @RequestMapping
    public String hello() {
        return "Hello Restlight Quarkus(SpringMVC)";
    }

}
```

#### Step 3: Add controller and start restlight

```java
@QuarkusMain
public class QuickStart implements QuarkusApplication {

    public static void main(String[] args) {
        Quarkus.run(QuickStart.class, args);
    }

    @Override
    public int run(String... args) {
        Restlight restlight = Restlight.forServer();
        restlight.deployments().addController(HelloController.class, false);
        restlight.address(9999);
        restlight.start();
        restlight.await();
        return 0;
    }
}
```

## Package To Native Image

In the Linux environment, run the following maven command to package the application into a `native image`:

```shell
mvn package -Pnative
```

After packaging, there will be a file that can be run directly in the `target` directory.

## Native Image vs Jar

Pack the code of QuickStart above into ordinary jar package and `native image`, and compare ordinary jar package and `native image`:

|       Operation mode       |  Jar   |    native image     |
| :------------------------: | :----: | :-----------------: |
|            Size            |  JDK alone has 200M+  | 49MB |
|  Time-consuming to start   | 2076ms |        41ms         |
| Occupies memory at runtime | 327MB  |        33MB         |