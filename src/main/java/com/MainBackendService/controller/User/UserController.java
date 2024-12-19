package com.MainBackendService.controller.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${apiPrefix}/users/")
public class UserController {

    Logger logger = LogManager.getLogger(UserController.class);


}
