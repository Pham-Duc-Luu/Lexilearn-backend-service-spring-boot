package com.MainBackendService.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public")
public class hello {
    Logger logger = LogManager.getLogger(hello.class);

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Pham duc luu!";
    }
}
