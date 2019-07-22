package com.cn.spring.service.workerimpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.spring.service.AuthenticationService;
import com.cn.spring.service.UserService;

@Service
public class DefaultAuthenticationService implements AuthenticationService {

    @Autowired
    UserService userService;

    @Override
    public Map<String, Object> validateUser(Map<String, Object> userDetails) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> addUser(Map<String, Object> userDetails) throws Exception {
        userService.saveUser(userDetails);
        return userDetails;
    }

}
