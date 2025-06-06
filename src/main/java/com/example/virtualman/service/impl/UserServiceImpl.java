package com.example.virtualman.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.virtualman.pojo.Result;
import com.example.virtualman.pojo.User;
import com.example.virtualman.mapper.UserMapper;
import com.example.virtualman.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-06-01
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Autowired
    private UserMapper mapper;

    //盐值 用于混淆加密
    private static final String SALT = "ChenLei";

    public static final String USER_LOGIN_STATE = "userLoginState";


    @Override
    public Result userRegister(String account, String password, String checkPassword) {
        //1.校验是否为空
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            throw new RuntimeException("参数不能为空");
        }


        //3.检验密码是否相同
        if (!password.equals(checkPassword)) {
            return Result.error("密码不相同");
        }


        //4.检验是否存在相同账号的成员，这里可以直接简要的写一个查询不必封装
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUserAccount, account);

        long count = 0;
        //直接查找数目，不用返回数据了
        count = mapper.selectCount(wrapper);
        if (count > 0) {
            return Result.error("已有账户存在");
        }

        //5.加密密码
        //用springboot自带的方法进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        //6.插入数据
        User newUser = new User();
        newUser.setUserAccount(account);
        newUser.setUserPassword(encryptPassword);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());

        int result = mapper.insert(newUser);

        if (result <= 0)//代表操作失败
        {
            return Result.error("插入数据错误，注册失败");
        }
//MyBatis-Plus在执行插入操作后，会从数据库中获取自动生成的ID，并将其设置回插入的对象
        return Result.success("恭喜你注册成功!!");
    }

    @Override
    public Result userLogin(String account, String password, HttpServletRequest request) {

        //1.校验是否为空
        if (StringUtils.isAnyBlank(account, password)) {
            log.info(account, password);
            return Result.error("账号或不能为空");
        }


        //4.加密密码
        //用springboot自带的方法进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        //5.查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUserAccount, account)
                .eq(User::getUserPassword, encryptPassword);

        User user;
        user = mapper.selectOne(wrapper);
        if (user == null) {
            return Result.error("账号或密码错误");
        } else {


            HttpSession session = request.getSession();
            log.info("Session ID: {}", session.getId());
            session.setAttribute(USER_LOGIN_STATE, user);


            //往session中设置的登录状态
//            request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
            log.info("存储Session成功,{}", user.toString());

            return Result.success("登录成功！！", user);
        }

    }


    @Override
    public long getUserId(HttpServletRequest request) {

        //从session中获取用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);


        if (userObj == null) {
            return 0;
        }

        //强转
        User user = (User) userObj;
        return user.getId();
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        //从session中获取用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            return null;
        }

        //强转
        return (User) userObj;
    }


    @Override
    public Boolean updateUser(User user, HttpServletRequest request) {


        Long userId = getUserId(request);
        log.error("userId为{}", userId);
        if (userId == 0) {
            log.error("没有userId");
            return false;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, userId);

        if (!StringUtils.isBlank(user.getUserPassword())) {
            //加密密码
            user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + user.getUserPassword()).getBytes()));
        }

        int result = mapper.update(user, wrapper);
        if (result <= 0) {

            return false;
        }


        //重新存放缓存
        User newUser = this.getById(userId);
        if (newUser == null) {
            return false;
        }
        request.getSession().setAttribute(USER_LOGIN_STATE, newUser);

        return true;
    }

    @Override
    public Result userLayout(HttpServletRequest request) {

        if (request.getSession().getAttribute(USER_LOGIN_STATE) != null) {
            request.getSession().removeAttribute(USER_LOGIN_STATE);
            return Result.success("账号已退出");
        }
        return Result.error(401, "账号未登录");
    }

    @Override
    public Result getAllUsers(HttpServletRequest request) {
        // 检查是否为管理员
        if (!isAdmin(request)) {
            return Result.error(403, "权限不足，仅管理员可操作");
        }

        // 查询所有用户，排除已删除的用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsDelete, 0)
                .orderByDesc(User::getCreateTime);

        List<User> userList = mapper.selectList(queryWrapper);
        // 清除敏感信息
        userList.forEach(user -> user.setUserPassword(null));

        return Result.success("查询成功", userList);
    }

    @Override
    public Result getUserById(Long id, HttpServletRequest request) {
        // 检查是否为管理员
        if (!isAdmin(request)) {
            return Result.error(403, "权限不足，仅管理员可操作");
        }

        if (id == null || id <= 0) {
            return Result.error(400, "用户ID无效");
        }

        User user = mapper.selectById(id);
        if (user == null || user.getIsDelete() == 1) {
            return Result.error(404, "用户不存在");
        }

        // 清除敏感信息
        user.setUserPassword(null);
        return Result.success("查询成功", user);
    }

    @Override
    public Result updateUserById(Long id, User user, HttpServletRequest request) {
        // 检查是否为管理员
        if (!isAdmin(request)) {
            return Result.error(403, "权限不足，仅管理员可操作");
        }

        if (id == null || id <= 0) {
            return Result.error(400, "用户ID无效");
        }

        // 检查用户是否存在
        User existUser = mapper.selectById(id);
        if (existUser == null || existUser.getIsDelete() == 1) {
            return Result.error(404, "用户不存在");
        }

        // 设置更新的字段
        User updateUser = new User();
        updateUser.setId(id);

        // 只更新非空字段
        if (user.getUsername() != null) {
            updateUser.setUsername(user.getUsername());
        }
        if (user.getUserAccount() != null) {
            // 检查账号是否已存在
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserAccount, user.getUserAccount())
                    .ne(User::getId, id)
                    .eq(User::getIsDelete, 0);
            long count = mapper.selectCount(wrapper);
            if (count > 0) {
                return Result.error(400, "账号已存在");
            }
            updateUser.setUserAccount(user.getUserAccount());
        }
        if (user.getAvatarUrl() != null) {
            updateUser.setAvatarUrl(user.getAvatarUrl());
        }
        if (user.getGender() != null) {
            updateUser.setGender(user.getGender());
        }
        if (user.getPhone() != null) {
            updateUser.setPhone(user.getPhone());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        if (user.getUserStatus() != null) {
            updateUser.setUserStatus(user.getUserStatus());
        }
        if (user.getRole() != null) {
            updateUser.setRole(user.getRole());
        }

        updateUser.setUpdateTime(LocalDateTime.now());

        int result = mapper.updateById(updateUser);
        if (result > 0) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @Override
    public Result deleteUserById(Long id, HttpServletRequest request) {
        // 检查是否为管理员
        if (!isAdmin(request)) {
            return Result.error(403, "权限不足，仅管理员可操作");
        }

        if (id == null || id <= 0) {
            return Result.error(400, "用户ID无效");
        }

        // 检查用户是否存在
        User user = mapper.selectById(id);
        if (user == null || user.getIsDelete() == 1) {
            return Result.error(404, "用户不存在");
        }

        // 不能删除自己
        User currentUser = getLoginUser(request);
        if (currentUser != null && currentUser.getId().equals(id)) {
            return Result.error(400, "不能删除自己的账号");
        }

        // 逻辑删除
        User deleteUser = new User();
        deleteUser.setId(id);
        deleteUser.setIsDelete(1);
        deleteUser.setUpdateTime(LocalDateTime.now());

        int result = mapper.updateById(deleteUser);
        if (result > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        return loginUser != null && loginUser.getRole() != null && loginUser.getRole() == 1;
    }

    @Override
    public Result searchUsers(String username, String email, Integer status, HttpServletRequest request) {
        // 检查是否为管理员
        if (!isAdmin(request)) {
            return Result.error(403, "权限不足，仅管理员可操作");
        }

        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsDelete, 0); // 只查询未删除的用户

        // 添加动态查询条件
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.eq(User::getEmail, email);
        }
        if (status != null) {
            queryWrapper.eq(User::getUserStatus, status);
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc(User::getCreateTime);

        // 执行查询
        List<User> userList = mapper.selectList(queryWrapper);

        // 清除敏感信息
        userList.forEach(user -> {
            user.setUserPassword(null);
        });

        log.info("管理员搜索用户，条件: username={}, email={}, status={}, 结果数量: {}",
                username, email, status, userList.size());

        return Result.success("查询成功", userList);
    }

}
