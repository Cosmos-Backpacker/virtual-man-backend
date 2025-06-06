package com.example.virtualman.service;

import com.example.virtualman.pojo.Media;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CosmosBackpacker
 * @since 2025-06-02
 */
@Service
public interface IMediaService extends IService<Media> {
    
    /**
     * 保存媒体记录
     */
    public boolean saveMedia(String taskId, String mediaUrl, Long userId);
    
    /**
     * 根据用户ID查询媒体记录
     */
    public List<Media> getMediaByUserId(Long userId);
    
    /**
     * 查询所有媒体记录（管理员权限）
     */
    public List<Media> getAllMedia();
    
    /**
     * 删除媒体记录
     */
    public boolean deleteMediaById(Long id, Long userId, boolean isAdmin);

}
