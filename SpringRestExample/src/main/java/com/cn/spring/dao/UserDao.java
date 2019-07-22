package com.cn.spring.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cn.spring.model.pojo.User;

@Component
public interface UserDao {
    
    public List<User> getAllUserList() throws Exception;
    
    public List<User> getRequestedUsersList(String userId) throws Exception;
    
    public User getUser(String userId) throws Exception;
    
    public int deleteUser(String userId) throws Exception;
    
    public int addUser(User user) throws Exception;
    

}
