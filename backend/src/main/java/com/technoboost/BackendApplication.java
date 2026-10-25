package com.technoboost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BackendApplication {

    static {
        // Pin the JVM to UTC so persisted date-times are identical on any host
        // (EC2, laptop, CI). User-facing zone conversion happens at the
        // response layer via the X-Timezone header.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
