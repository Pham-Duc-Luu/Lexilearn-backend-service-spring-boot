package com.MainBackendService.controller;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("")
public class hello {
    Logger logger = LogManager.getLogger(hello.class);

    @GetMapping("")
    public String hello() {
        return "Hello, Pham duc luu!";
    }
}
