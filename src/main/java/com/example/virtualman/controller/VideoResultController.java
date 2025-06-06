package com.example.virtualman.controller;

import com.example.virtualman.pojo.Result;
import com.example.virtualman.service.IMediaService;
import com.example.virtualman.service.VideoResultService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/video/result")
public class VideoResultController {

    @Autowired
    private VideoResultService videoResultService;


    @PostMapping("/url")
    public Result getVideoUrl(@RequestParam String taskId) {
        Map<String, String> result = videoResultService.getCompletedVideoUrl(taskId);

        if (result.containsKey("Success")) {
            return Result.success("视频生成成功", result.get("Success"));
        } else if (result.containsKey("Making")) {
            return Result.success("视频生成中", result.get("Making"));
        } else if (result.containsKey("Error")) {
            return Result.error("视频生成失败" + result.get("Error"));
        }
        return Result.error("未知错误");
    }




}