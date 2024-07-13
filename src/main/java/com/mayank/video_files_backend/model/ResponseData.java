package com.mayank.video_files_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {

    private String file_name;
    private String download_URL;
    private String file_type;
    private long file_size;
}