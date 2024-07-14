package com.mayank.video_files_backend.service;

import com.mayank.video_files_backend.entity.Attachment;
import com.mayank.video_files_backend.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentServiceImpl implements AttachmentService{
    private AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Attachment saveAttachment(MultipartFile file) throws Exception {
        String file_name = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if(file_name.contains("..")){
                throw new Exception("Filename contains invalid path sequence" + file_name);
            }
            Attachment attachment = new Attachment(file_name, file.getContentType(), file.getBytes());

            return attachmentRepository.save(attachment);
        } catch (Exception e){
            throw new Exception("Could not save file" + file_name);
        }
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository
                .findById(fileId)
                .orElseThrow(() -> new Exception("File not found with Id" + fileId));
    }


}