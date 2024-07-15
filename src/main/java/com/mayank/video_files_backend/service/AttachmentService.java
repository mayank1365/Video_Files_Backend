package com.mayank.video_files_backend.service;

import com.mayank.video_files_backend.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file) throws Exception;
    Attachment getAttachment(String fileId) throws Exception;
    Attachment trimVideo(String fileId, String startTime, String endTime) throws Exception;
    Attachment mergeVideos(List<String> fileIds) throws Exception;
    void saveShareToken(String fileId, String token, long expiryTime);
    Attachment getSharedAttachment(String token) throws Exception;
}
