package com.cn.spring.test;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.cn.spring.constant.RestURIConstants;
import com.cn.spring.model.pojo.User;

public class TestDateme {

    public static final String SERVER_URI = "http://localhost:8080/dateme";

    public static void main(String args[]) {

        testGetDummyUser();
        System.out.println("*****");
        testCreateUser();
        System.out.println("*****");
        testGetUser();
        System.out.println("*****");
        testGetAllUsers();
    }

    private static void testGetAllUsers() {
        RestTemplate rest = new RestTemplate();
        // we can't get List<Employee> because JSON convertor doesn't know the type of
        // object in the list and hence convert it to default JSON object type LinkedHashMap
        List<LinkedHashMap> users = rest.getForObject(SERVER_URI + RestURIConstants.GET_ALL_USER, List.class);
        System.out.println(users.size());
        for (LinkedHashMap map : users) {
            System.out.println("Name=" + map.get("name") + ",CreatedDate=" + map.get("createdDate"));;
        }
    }

    private static void testCreateUser() {
        RestTemplate rest = new RestTemplate();
        User user = new User();
        user.setUserId("Chetan Nehete");
        User response = rest.postForObject(SERVER_URI + RestURIConstants.CREATE_USER, user, User.class);
        printUserData(response);
    }

    private static void testGetUser() {
        RestTemplate rest = new RestTemplate();
        User user = rest.getForObject(SERVER_URI + "/rest/user/1", User.class);
        printUserData(user);
    }

    private static void testGetDummyUser() {
        RestTemplate rest = new RestTemplate();
        User user = rest.getForObject(SERVER_URI + RestURIConstants.DUMMY_USER, User.class);
        printUserData(user);
    }

    public static void printUserData(User user) {
        System.out.println("Name=" + user.getUserId() + ",CreatedDate=" + user.getCreatedon());
    }
}
