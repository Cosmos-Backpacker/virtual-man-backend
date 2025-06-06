package com.example.virtualman.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.virtualman.pojo.Media;
import com.example.virtualman.mapper.MediaMapper;
import com.example.virtualman.service.IMediaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-06-02
 */
@Service
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media> implements IMediaService {

    @Autowired
    private MediaMapper mediaMapper;

    @Override
    public boolean saveMedia(String taskId, String mediaUrl, Long userId) {
        Media media = new Media();
        media.setTaskId(taskId);
        media.setMediaUrl(mediaUrl);
        media.setUserId(userId);
        media.setCreateTime(LocalDateTime.now());
        int result = mediaMapper.insert(media);
        return result > 0;
    }

    @Override
    public List<Media> getMediaByUserId(Long userId) {
        LambdaQueryWrapper<Media> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Media::getUserId, userId)
                   .orderByDesc(Media::getCreateTime);
        return mediaMapper.selectList(queryWrapper);
    }

    @Override
    public List<Media> getAllMedia() {
        LambdaQueryWrapper<Media> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Media::getCreateTime);
        return mediaMapper.selectList(queryWrapper);
    }

    @Override
    public boolean deleteMediaById(Long id, Long userId, boolean isAdmin) {
        // 如果不是管理员，需要验证记录是否属于当前用户
        if (!isAdmin) {
            LambdaQueryWrapper<Media> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Media::getId, id)
                       .eq(Media::getUserId, userId);
            Media media = mediaMapper.selectOne(queryWrapper);
            if (media == null) {
                return false; // 记录不存在或不属于当前用户
            }
        }
        
        int result = mediaMapper.deleteById(id);
        return result > 0;
    }
}
