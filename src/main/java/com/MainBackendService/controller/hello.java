package com.MainBackendService.controller;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class hello {
    Logger logger = LogManager.getLogger(hello.class);

    @Value("${API_KEY}")
    private  String VALID_API_KEY ; // Replace with your actual API key

    @GetMapping("/hello")
    public String hello() {


        logger.debug(VALID_API_KEY);


        return "Hello, Spring Boot!";
    }
}
