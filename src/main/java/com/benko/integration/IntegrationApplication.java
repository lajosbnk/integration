package com.benko.integration;

import com.flexionmobile.codingchallenge.integration.Integration;
import com.flexionmobile.codingchallenge.integration.IntegrationTestRunner;
import com.flexionmobile.codingchallenge.integration.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
public class IntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }

//    @Component
//    class Start implements CommandLineRunner {
//
//        private final Integration flexionIntegration;
//
//        @Autowired
//        Start(Integration flexionIntegration) {
//            this.flexionIntegration = flexionIntegration;
//        }
//
//        @Override
//        public void run(String... args) throws Exception {
//            IntegrationTestRunner testRunner = new IntegrationTestRunner();
//            testRunner.runTests(flexionIntegration);
//        }
//    }
}
