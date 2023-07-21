package beanvest;

import bb.lib.testing.apprunner.JarRunner;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

public class BeanvestEnd2EndTest extends BeanvestAcceptanceTest {

    @BeforeEach
    void setUpE2E() {
        runner = new JarRunner("../../gradlew", Optional.of("build/libs/beanvest-1.0-SNAPSHOT.jar"));
    }
}