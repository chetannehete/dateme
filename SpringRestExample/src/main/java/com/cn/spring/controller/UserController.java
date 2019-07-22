package com.cn.spring.controller;

import static com.cn.spring.constant.RestURIConstants.CREATE_USER;
import static com.cn.spring.constant.RestURIConstants.DELETE_USER;
import static com.cn.spring.constant.RestURIConstants.DUMMY_USER;
import static com.cn.spring.constant.RestURIConstants.GET_ALL_USER;
import static com.cn.spring.constant.RestURIConstants.GET_USER;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.spring.model.pojo.User;
import static com.cn.spring.util.CollectionUtil.map;
import com.cn.spring.service.UserService;

/**
 * Handles requests for the User service.
 */
@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    // Map to store Users, ideally we should use database
    Map<String, Map<String, Object>> userData = new HashMap<String, Map<String, Object>>();

    @RequestMapping(value = DUMMY_USER, method = { RequestMethod.GET, RequestMethod.POST} )
    public @ResponseBody Map<String, Object> getDummyUser() {
        logger.info("Start getDummyUser");
        userData.put("Dummy", (Map<String, Object>) map("userId", "chandu", "date", new Date()));
        return  (Map<String, Object>) map("userId", "chandu", "date", new Date());
    }

    @RequestMapping(value = GET_USER, method ={ RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody Map<String, Object> getUser(@RequestBody Map<String, Object> userDetails) throws Exception {
        String name = (String) userDetails.get("userId");
        logger.info("Start getUser. ID=" + name);
        return userService.getUser(name);
    }

    @RequestMapping(value = GET_ALL_USER, method ={ RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Map<String, Object>> getAllUsers() {
        logger.info("Start getAllUsers.");
        List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
        Set<String> userIdKeys = userData.keySet();
        for (String i : userIdKeys) {
            users.add(userData.get(i));
        }
        return users;
    }

    @RequestMapping(value = CREATE_USER, method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> createUser(@RequestBody Map<String, Object> userDetails) throws Exception {
        logger.info("Start createUser.");
        return userService.saveUser(userDetails);
    }

    @RequestMapping(value = DELETE_USER, method = RequestMethod.PUT)
    public @ResponseBody Map<String, Object> deleteUser(@RequestBody Map<String, Object> userDetails) {
        logger.info("Start deleteUser.");
        String userId = (String) userDetails.get("name");
        Map<String, Object> user = userData.get(userId);
        userData.remove(userId);
        return user;
    }

}
