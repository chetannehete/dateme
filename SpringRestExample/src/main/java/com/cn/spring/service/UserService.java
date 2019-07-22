package com.cn.spring.service;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public interface UserService {

    public Map<String, Object> saveUser(Map<String,Object> userDetails) throws Exception;

    public Map<String, Object> getUser(String name) throws Exception;
    
   
}
