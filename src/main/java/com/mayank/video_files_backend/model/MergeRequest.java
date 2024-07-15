package com.mayank.video_files_backend.model;

import java.util.List;

public class MergeRequest {
    private List<String> fileIds;

    public MergeRequest() {
    }

    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }
}

