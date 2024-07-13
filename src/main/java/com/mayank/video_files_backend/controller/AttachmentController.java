package com.mayank.video_files_backend.controller;

import com.mayank.video_files_backend.entity.Attachment;
import com.mayank.video_files_backend.model.ResponseData;
import com.mayank.video_files_backend.service.AttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public ResponseData uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        Attachment attachment = attachmentService.saveAttachment(file);
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(attachment.getId())
                .toUriString();

        return new ResponseData(
                attachment.getFile_name(),
                downloadUrl,
                file.getContentType(),
                file.getSize());
    }

}