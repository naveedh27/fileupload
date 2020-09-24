package com.fileupload.service.integration;

import com.fileupload.FileUploadApplication;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:format"}, strict = true,
        features = "src/test/java/com/fileupload/service/integration/fileupload.feature",
        glue = {"com.fileupload.service.integration"})
public class FileUploadRunnerIT {

    private static ConfigurableApplicationContext run;

    @BeforeClass
    public static void beforeClass() throws IOException {
        run = SpringApplication.run(FileUploadApplication.class);
    }

    @AfterClass
    public static void afterClass() {
        run.stop();
    }

}
