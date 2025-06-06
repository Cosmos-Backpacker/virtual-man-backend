package com.example.virtualman.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VideoCreationService {

    private final VideoMakerService videoMakerService;

    // 如果使用构造器注入
    public VideoCreationService(VideoMakerService videoMakerService) {
        this.videoMakerService = videoMakerService;
    }

    /**
     * 创建视频的普通方法
     * @param virtualmanKey 虚拟主播密钥
     * @param ssmlText 要合成的SSML格式文本
     * @param speed 语速
     * @return 视频生成任务ID
     * @throws Exception 视频生成过程中可能出现的异常
     */
    public String createVideo(String virtualmanKey, String ssmlText, float speed) throws Exception {
        return videoMakerService.createVideoWithText(virtualmanKey, ssmlText, speed);
    }

    /**
     * 使用默认参数的简化创建视频方法
     * @return 视频生成任务ID
     * @throws Exception 视频生成过程中可能出现的异常
     */
    public String createDefaultVideo() throws Exception {
        String virtualmanKey = "487ebcd75d1243bdbc03cdbe0fb694b2";
        String ssmlText = "你好，我是虚拟主播，一种基于大模型处理的多维健康数据筛选整合方法";
        float speed = 1.0f;
        
        return createVideo(virtualmanKey, ssmlText, speed);
    }
}
