package com.example.virtualman.service;

import com.example.virtualman.pojo.Result;
import com.example.virtualman.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-06-01
 */
@Service
public interface IUserService extends IService<User> {

    Result userRegister(String account, String password, String checkPassword);

    /**
     * 用户登录
     *
     * @param account  登录账号
     * @param password 登录密码
     * @param request  请求参数，存储session
     * @return
     */
    Result userLogin(String account, String password, HttpServletRequest request);


    public long getUserId(HttpServletRequest request);
    
    /**
     * 获取当前登录用户信息
     */
    public User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求参数
     * @return 注销结果
     */
    public Result userLayout(HttpServletRequest request);
    
    /**
     * 管理员查询所有用户信息
     */
    public Result getAllUsers(HttpServletRequest request);
    
    /**
     * 管理员根据ID查询用户信息
     */
    public Result getUserById(Long id, HttpServletRequest request);
    
    /**
     * 管理员更新用户信息
     */
    public Result updateUserById(Long id, User user, HttpServletRequest request);
    
    /**
     * 管理员删除用户
     */
    public Result deleteUserById(Long id, HttpServletRequest request);


    public Boolean updateUser(User user, HttpServletRequest request);

    /**
     * 检查当前用户是否为管理员
     */
    public boolean isAdmin(HttpServletRequest request);

    /**
     * 管理员根据条件搜索用户
     * @param username 用户名（模糊查询）
     * @param email 邮箱（精确查询）
     * @param status 用户状态
     * @param request 请求
     * @return 符合条件的用户列表
     */
    public Result searchUsers(String username, String email, Integer status, HttpServletRequest request);

}
