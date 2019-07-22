package com.cn.spring.service.workerimpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.spring.dao.UserDao;
import com.cn.spring.model.pojo.User;
import com.cn.spring.service.UserService;
import static com.cn.spring.util.ConversionUtil.mapToObject;

@Service
public class DefaultUserService implements UserService {
    @Autowired
    UserDao userDao;
    
    @Override
    public Map<String, Object> saveUser(Map<String, Object> userDetails) throws Exception {
        User user = mapToObject(userDetails, User.class);
        userDao.addUser(user);
        return userDetails;
    }

    @Override
    public Map<String, Object> getUser(String name) throws Exception {
         User user = userDao.getUser(name);
         return mapToObject(user, Map.class);
    }

}
