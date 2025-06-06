package com.example.virtualman.controller;

import com.example.virtualman.pojo.Result;
import com.example.virtualman.pojo.User;
import com.example.virtualman.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-06-01
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;


    @PostMapping("/register")
    public Result register(String userAccount, String password, String checkPassword) {

        return userService.userRegister(userAccount, password, checkPassword);

    }


    @PostMapping("/login")
    public Result login(String userAccount, String userPassword, HttpServletRequest request) {
        return userService.userLogin(userAccount, userPassword, request);

    }


    /**
     * 用户注销
     *
     * @param request 请求
     * @return 结果
     */
    @PostMapping("/logout")
    public Result userLogout(HttpServletRequest request) {
        return userService.userLayout(request);
    }


    @PostMapping("/updateInfo")
    public Result updateUser(@RequestBody User user, HttpServletRequest request) {

        if (user == null) {
            throw new RuntimeException("参数为空");
        }

        long userId = userService.getUserId(request);
        if (userId == 0) {
            return Result.error(401, "用户未登录");
        }


        if (userService.updateUser(user, request)) {
            //根据实际需求前端更新完成之后需要重新获取更新后的对象
            User newUser = userService.getById(userId);

            return Result.success("修改成功", newUser);
        } else {
            return Result.error("修改失败");
        }
    }


    /**
     * 管理员查询所有用户
     */
    @GetMapping("/admin/list")
    public Result getAllUsers(HttpServletRequest request) {
        return userService.getAllUsers(request);
    }

    /**
     * 管理员根据ID查询用户
     */
    @GetMapping("/admin/{id}")
    public Result getUserById(@PathVariable Long id, HttpServletRequest request) {
        return userService.getUserById(id, request);
    }

    /**
     * 管理员更新用户信息
     */
    @PutMapping("/admin/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        return userService.updateUserById(id, user, request);
    }

    /**
     * 管理员删除用户
     */
    @DeleteMapping("/admin/{id}")
    public Result deleteUser(@PathVariable Long id, HttpServletRequest request) {
        return userService.deleteUserById(id, request);
    }

    /**
     * 管理员根据条件搜索用户
     *
     * @param username 用户名（模糊查询）
     * @param email    邮箱（精确查询）
     * @param status   用户状态
     * @param request  请求
     * @return 用户列表
     */
    @GetMapping("/admin/search")
    public Result searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        return userService.searchUsers(username, email, status, request);
    }

}
