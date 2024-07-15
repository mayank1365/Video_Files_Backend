package com.mayank.video_files_backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrimRequest {
    private String fileId;
    private String startTime;
    private String endTime;

    public TrimRequest(String fileId, String startTime, String endTime) {
        this.fileId = fileId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
