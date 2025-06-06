package com.example.virtualman.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.example.virtualman.utils.Presigned;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class VideoMakerService {

    @Autowired
    private Presigned presigned;

    /**
     * 使用文本驱动方式生成视频
     *
     * @param virtualmanKey 虚拟主播key
     * @param ssmlText      SSML格式文本
     * @param speed         语速(0.5-1.5)
     * @return 任务ID
     */
    public String createVideoWithText(String virtualmanKey, String ssmlText, float speed) {

        String API_URL = presigned.getReqUrl();

        // 构建简化请求体
        String requestBody = String.format(
                "{\"Header\":{},\"Payload\":{" +
                        "\"VirtualmanKey\":\"%s\"," +
                        "\"InputSsml\":\"%s\"," +
                        "\"SpeechParam\":{\"Speed\":%.1f}," +
                        "\"DriverType\":\"Text\"}}",
                virtualmanKey, ssmlText, speed);

        log.error("Request Body: {}", requestBody);

        // 发送HTTP请求
        String response = HttpRequest.post(API_URL)
                .header("Content-Type", "application/json;charset=utf-8")
                .body(requestBody)
                .execute()
                .body();

        log.info("API Response: {}", response);

        Map<String, Object> responseMap = JSONUtil.parseObj(response);
        if (responseMap.containsKey("Header")) {
            Map<String, Object> header = (Map<String, Object>) responseMap.get("Header");
            if (header.containsKey("Code") && (int) header.get("Code") == 0) {
                log.info("视频生成任务已创建，任务ID: {}", responseMap.get("Payload").toString());
                if (responseMap.containsKey("Payload")) {
                    Map<String, Object> payload = (Map<String, Object>) responseMap.get("Payload");
                    if (payload.containsKey("TaskId")) {
                        return payload.get("TaskId").toString();
                    }
                }
            } else {
                log.error("视频生成失败: {}", header.get("Message"));
                return "生成视频任务创建失败";
            }
        }

        return "生成视频任务创建失败";
    }
}
