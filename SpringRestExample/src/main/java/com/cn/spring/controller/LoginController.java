package com.cn.spring.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.spring.constant.RestURIConstants;
import com.cn.spring.service.AuthenticationService;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    AuthenticationService authenticationService;

    @RequestMapping(value = RestURIConstants.VALIDATE_USER, method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody Map<String, Object> validateUser(@RequestBody Map<String, Object> userDetails) throws Exception {
        logger.info("Start getUser. ID=" + userDetails.get("userId"));
        return authenticationService.validateUser(userDetails);
    }

    @RequestMapping( value = RestURIConstants.REGISTER_USER, method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody Map<String, Object> registerUser(@RequestBody Map<String, Object> userDetails) throws Exception {
        logger.info("Start getUser. ID=" + userDetails.get("userId"));
        return authenticationService.addUser(userDetails);
    }
}
