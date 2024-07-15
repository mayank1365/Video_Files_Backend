package com.mayank.video_files_backend.service;

import com.mayank.video_files_backend.entity.Attachment;
import com.mayank.video_files_backend.model.ShareToken;
import com.mayank.video_files_backend.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ConcurrentHashMap<String, ShareToken> tokenStore = new ConcurrentHashMap<>();

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Attachment saveAttachment(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        try {
            Attachment attachment = new Attachment();
            attachment.setId(UUID.randomUUID().toString());
            attachment.setFile_name(fileName);
            attachment.setFile_type(file.getContentType());
            attachment.setData(file.getBytes());
            return attachmentRepository.save(attachment);
        } catch (Exception e) {
            throw new Exception("Could not save file " + fileName, e);
        }
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found with id " + fileId));
    }

    @Override
    public Attachment trimVideo(String fileId, String startTime, String endTime) throws Exception {
        Attachment attachment = getAttachment(fileId);
        File inputFile = convertToFile(attachment);
        File outputFile = new File("trimmed_" + inputFile.getName());

        String command = String.format("\"C:\\ffmpeg\\bin\\ffmpeg.exe\" -i \"%s\" -ss %s -to %s -c copy \"%s\"",
                inputFile.getAbsolutePath(), startTime, endTime, outputFile.getAbsolutePath());

        executeFFmpegCommand(command);

        byte[] trimmedData = java.nio.file.Files.readAllBytes(outputFile.toPath());
        Attachment trimmedAttachment = new Attachment(
                "trimmed_" + attachment.getFile_name(), attachment.getFile_type(), trimmedData);
        return attachmentRepository.save(trimmedAttachment);
    }

    @Override
    public Attachment mergeVideos(List<String> fileIds) throws Exception {
        System.out.println("Merging videos with ids: " + fileIds);
        List<File> inputFiles = fileIds.stream()
                .map(id -> {
                    try {
                        return convertToFile(getAttachment(id));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to fetch attachment with id: " + id, e);
                    }
                }).toList();
        File concatFile = createConcatFile(inputFiles);
        File outputFile = new File("merged_output.mp4");

        String command = String.format("\"C:\\ffmpeg\\bin\\ffmpeg.exe\" -f concat -safe 0 -i \"%s\" -c copy \"%s\"",
                concatFile.getAbsolutePath(), outputFile.getAbsolutePath());

        executeFFmpegCommand(command);

        byte[] mergedData = java.nio.file.Files.readAllBytes(outputFile.toPath());
        Attachment mergedAttachment = new Attachment(
                "merged_output.mp4", "video/mp4", mergedData);
        return attachmentRepository.save(mergedAttachment);
    }

    private File convertToFile(Attachment attachment) throws IOException {
        File file = new File(attachment.getFile_name());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(attachment.getData());
        }
        return file;
    }

    private File createConcatFile(List<File> inputFiles) throws IOException {
        File concatFile = new File("concat.txt");
        try (FileOutputStream fos = new FileOutputStream(concatFile)) {
            for (File file : inputFiles) {
                String line = "file '" + file.getAbsolutePath() + "'\n";
                fos.write(line.getBytes());
            }
        }
        return concatFile;
    }

    private void executeFFmpegCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.inheritIO();
        Process process = processBuilder.start();

        // Capture and log any error messages from the process
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg command failed with exit code " + exitCode);
        }
    }
}
