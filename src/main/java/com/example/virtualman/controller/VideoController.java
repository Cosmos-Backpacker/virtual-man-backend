package com.example.virtualman.controller;

import com.example.virtualman.pojo.Result;
import com.example.virtualman.service.VideoMakerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoMakerService videoMakerService;

    @PostMapping("/create") // 修改为 PostMapping 以支持文件上传和表单数据
    public Result createVideo(
            @RequestParam("virtualmanKey") String virtualmanKey,
            @RequestParam(value = "ssmlText", required = false) String ssmlText,
            @RequestParam(value = "speed", defaultValue = "1.0") float speed,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        // 如果没有传入文本，尝试从文件中读取
        if (ssmlText == null && file != null && !file.isEmpty()) {
            try {
                ssmlText = extractTextFromFile(file);
            } catch (IOException e) {
                log.error("读取文件失败: {}", e.getMessage());
                return Result.error("读取文件失败: " + e.getMessage());
            } catch (Exception e) {
                log.error("处理文件失败: {}", e.getMessage());
                return Result.error("处理文件失败: " + e.getMessage());
            }
        }

        // 检查是否有有效的文本输入
        if (ssmlText == null || ssmlText.isEmpty()) {
            return Result.error("请提供有效的文本内容或上传文件");
        }

        try {
            String taskId = videoMakerService.createVideoWithText(virtualmanKey, ssmlText, speed);
            return Result.success("创建视频成功", taskId);
        } catch (Exception e) {
            return Result.error("创建视频失败");
        }
    }

    /**
     * 从文件中提取文本内容
     * 支持 .txt, .doc, .docx 格式
     */
    private String extractTextFromFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();
        InputStream inputStream = file.getInputStream();

        switch (fileExtension) {
            case "txt":
                // 处理 TXT 文件
                return new String(file.getBytes(), StandardCharsets.UTF_8);
            case "docx":
                // 处理 DOCX 文件 (新版 Word 格式)
                try (XWPFDocument document = new XWPFDocument(inputStream);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    return extractor.getText();
                }

            default:
                throw new IllegalArgumentException("不支持的文件格式: " + fileExtension + "。仅支持 .txt, .doc, .docx 格式");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

}
