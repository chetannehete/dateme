package com.cn.spring.service;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public interface AuthenticationService {

    public Map<String, Object> validateUser(Map<String,Object> userDetails) throws Exception;
    
    public Map<String, Object> addUser(Map<String,Object> userDetails) throws Exception;
}
