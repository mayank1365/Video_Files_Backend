package com.mayank.video_files_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import org.hibernate.annotations.GenericGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String file_name;
    private String file_type;

    @Lob
    private byte[] data;

    public Attachment(String file_name, String file_type, byte[] data) {
        this.file_name = file_name;
        this.file_type = file_type;
        this.data = data;
    }
}