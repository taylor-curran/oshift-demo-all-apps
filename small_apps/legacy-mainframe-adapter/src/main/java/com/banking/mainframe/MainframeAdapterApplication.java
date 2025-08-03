package com.banking.mainframe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainframeAdapterApplication {
    
    public static void main(String[] args) {
        // Load IBM CICS libraries
        System.setProperty("java.library.path", "/opt/ibm/cics");
        SpringApplication.run(MainframeAdapterApplication.class, args);
    }
}
