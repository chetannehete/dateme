package com.cn.spring.daoimpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cn.spring.dao.UserDao;
import com.cn.spring.model.pojo.User;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<User> getAllUserList() throws Exception {
        return null;
    }

    @Override
    public List<User> getRequestedUsersList(String userId) throws Exception {
        return null;
    }

    @Override
    public User getUser(String userId) throws Exception {
        String query = "select * from user where userId = ?";
        User user = new User();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            con = dataSource.getConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if(rs.next()){
                user = new User();
                user.setUserId(rs.getString("userId"));
                user.setMobile(rs.getInt("mobile"));
                user.setEmail(rs.getString("email"));
                user.setGender(rs.getString("gender"));
                user.setStatus(rs.getString("status"));
                user.setLattitude(rs.getString("lattitude"));
                user.setLongitude(rs.getString("longitude"));
                user.setLocation(rs.getString("location"));
                user.setChatrequest(rs.getString("chatrequest"));
                user.setLastupdatetime(rs.getDate("lastupdatetime"));
                user.setCreatedon(rs.getDate("createdon"));
                System.out.println("User Found::"+user);
            }else{
                System.out.println("No User found with id="+userId);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public int deleteUser(String userId) throws Exception {
        return 0;
    }

    @Override
    public int addUser(User user) throws Exception {
        String sql = "INSERT INTO USER (userId,mobile,email,gender,status,lattitude,longitude,location,chatrequest,lastupdatetime,createdon,password) " + "VALUES (?, ?, ?,?, ?, ?,?, ?, ?,?, ?, ?)";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            ps.setInt(2, user.getMobile());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getStatus());
            ps.setString(6, user.getLattitude());
            ps.setString(7, user.getLongitude());
            ps.setString(8, user.getLocation());
            ps.setString(9, user.getChatrequest());
            ps.setDate(10, new Date(System.currentTimeMillis()));
            ps.setDate(11, new Date(System.currentTimeMillis()));
            ps.setString(12, user.getPassword());
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return -1;
                }
            }
        }
        return 1;
    }

}
