package com.example.virtualman.controller;


import com.example.virtualman.pojo.Media;
import com.example.virtualman.pojo.Result;
import com.example.virtualman.pojo.User;
import com.example.virtualman.service.IMediaService;
import com.example.virtualman.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-06-02
 */
@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private IMediaService mediaService;

    @Autowired
    private IUserService userService;

    /**
     * 保存 taskId 和 mediaUrl 到数据库
     *
     * @param taskId   任务 ID
     * @param mediaUrl 媒体 URL
     * @return 返回保存结果
     */
    @PostMapping("/saveMedia")
    public Result saveMedia(@RequestParam String taskId, @RequestParam String mediaUrl, HttpServletRequest request) {

        Long userId = userService.getUserId(request);

        if (userId == 0) {
            return Result.error(400,"用户未登录");
        }


        boolean success = mediaService.saveMedia(taskId, mediaUrl, userId);
        if (success) {
            return Result.success("数据保存成功");
        } else {
            return Result.error("数据保存失败");
        }
    }
    
    /**
     * 查询用户的媒体记录
     * 普通用户只能查询自己的记录，管理员可以查询所有记录
     */
    @GetMapping("/list")
    public Result getMediaList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return Result.error(401, "用户未登录");
        }
        
        List<Media> mediaList;
        // 判断是否为管理员（role = 1）
        if (loginUser.getRole() != null && loginUser.getRole() == 1) {
            // 管理员查询所有记录
            mediaList = mediaService.getAllMedia();
        } else {
            // 普通用户查询自己的记录
            mediaList = mediaService.getMediaByUserId(loginUser.getId());
        }
        
        return Result.success("查询成功", mediaList);
    }
    
    /**
     * 删除媒体记录
     * 普通用户只能删除自己的记录，管理员可以删除任意记录
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteMedia(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return Result.error(401, "用户未登录");
        }
        
        // 判断是否为管理员
        boolean isAdmin = loginUser.getRole() != null && loginUser.getRole() == 1;
        
        boolean success = mediaService.deleteMediaById(id, loginUser.getId(), isAdmin);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败，记录不存在或无权限删除");
        }
    }

}
