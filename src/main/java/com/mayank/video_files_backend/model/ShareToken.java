package com.mayank.video_files_backend.model;

public class ShareToken {
    private final String fileId;
    private final long expiryTime;

    public ShareToken(String fileId, long expiryTime) {
        this.fileId = fileId;
        this.expiryTime = expiryTime;
    }

    public String getFileId() {
        return fileId;
    }

    public long getExpiryTime() {
        return expiryTime;
    }
}
