package com.example.virtualman.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.example.virtualman.utils.Presigned;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;


@Slf4j
@Service
public class VideoResultService {


    @Autowired
    private Presigned presigned;

    /**
     * 获取完成的视频结果URL
     *
     * @param taskId 任务ID
     * @return 视频结果URL，如果未完成则返回null
     */
    public Map<String, String> getCompletedVideoUrl(String taskId) {
        Map<String, Object> response = queryProgressSimple(taskId);
        Map<String, Object> header = (Map<String, Object>) response.get("Header");

        if (response.containsKey("Payload")) {
            Map<String, Object> payload = (Map<String, Object>) response.get("Payload");
            if (payload.containsKey("Status") && (payload.get("Status").toString().equals("SUCCESS"))) {
                log.info("视频制作完成");
                return Map.of("Success", (String) payload.get("MediaUrl"));
            } else if (payload.containsKey("Status") && (payload.get("Status").toString().equals("MAKING"))) {
                log.info("视频制作中");
                return Map.of("Making", "视频制作中,请耐心等待");
            }
        } else {
            String message = (String) header.get("Message");
            log.info("出错了{}", message);
            return Map.of("Error", message);
        }

        return Map.of("Error", "出错了");
    }


    private Map<String, Object> queryProgressSimple(String taskId) {
        String requestBody = String.format("{\"Header\":{},\"Payload\":{\"TaskId\":\"%s\"}}", taskId);

        String responseBody = HttpRequest.post(presigned.getReqProgressUrl())
                .header("Content-Type", "application/json;charset=utf-8")
                .body(requestBody)
                .execute()
                .body();

        log.error("responseBody为{}", responseBody);
        return JSONUtil.parseObj(responseBody);
    }

}
