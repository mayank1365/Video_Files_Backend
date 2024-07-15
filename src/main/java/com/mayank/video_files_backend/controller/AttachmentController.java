package com.mayank.video_files_backend.controller;

import com.mayank.video_files_backend.entity.Attachment;
import com.mayank.video_files_backend.model.MergeRequest;
import com.mayank.video_files_backend.model.ResponseData;
import com.mayank.video_files_backend.model.TrimRequest;
import com.mayank.video_files_backend.service.AttachmentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    private boolean isValidToken(String authToken) {
        return authToken != null && authToken.equals("Bearer user@Auth");
    }

    @PostMapping("/upload")
    public ResponseData uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authToken) throws Exception {
        if (!isValidToken(authToken)) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        Attachment attachment = attachmentService.saveAttachment(file);
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(attachment.getId())
                .toUriString();

        return new ResponseData(
                attachment.getFile_name(),
                downloadUrl,
                attachment.getId(),
                file.getContentType(),
                file.getSize());
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, @RequestHeader("Authorization") String authToken) throws Exception {
        if (!isValidToken(authToken)) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        Attachment attachment = attachmentService.getAttachment(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFile_type()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFile_name() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

    @PostMapping("/trim")
    public ResponseData trimVideo(@RequestBody TrimRequest trimRequest, @RequestHeader("Authorization") String authToken) throws Exception {
        if (!isValidToken(authToken)) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        String fileId = trimRequest.getFileId();
        String startTime = trimRequest.getStartTime();
        String endTime = trimRequest.getEndTime();

        Attachment trimmedVideo = attachmentService.trimVideo(fileId, startTime, endTime);

        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(trimmedVideo.getId())
                .toUriString();

        return new ResponseData(
                trimmedVideo.getFile_name(),
                downloadUrl,
                trimmedVideo.getId(),
                trimmedVideo.getFile_type(),
                trimmedVideo.getData().length);
    }

    @PostMapping("/merge")
    public ResponseData mergeVideos(@RequestBody MergeRequest mergeRequest, @RequestHeader("Authorization") String authToken) throws Exception {
        if (!isValidToken(authToken)) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        Attachment mergedAttachment = attachmentService.mergeVideos(mergeRequest.getFileIds());
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(mergedAttachment.getId())
                .toUriString();

        return new ResponseData(
                mergedAttachment.getFile_name(),
                downloadUrl,
                mergedAttachment.getId(),
                mergedAttachment.getFile_type(),
                mergedAttachment.getData().length);
    }

    @GetMapping("/share/{fileId}")
    public String shareLink(@PathVariable String fileId, @RequestHeader("Authorization") String authToken) {
        if (!isValidToken(authToken)) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        String token = UUID.randomUUID().toString();
        long expiryTime = System.currentTimeMillis() + (1000 * 60 * 60); // 1 hour from now
        attachmentService.saveShareToken(fileId, token, expiryTime);

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/shared/")
                .path(token)
                .toUriString();
    }

    @GetMapping("/shared/{token}")
    public ResponseEntity<Resource> accessSharedFile(@PathVariable String token, @RequestHeader("Authorization") String authToken) throws Exception {
        if (!isValidToken(authToken)) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        Attachment attachment = attachmentService.getSharedAttachment(token);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFile_type()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFile_name() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

    // Exception handler for UnauthorizedException
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // Custom exception class for Unauthorized access
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
