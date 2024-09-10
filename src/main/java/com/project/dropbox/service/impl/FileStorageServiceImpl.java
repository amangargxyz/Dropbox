package com.project.dropbox.service.impl;

import com.project.dropbox.model.FileMetaData;
import com.project.dropbox.repository.FileMetaDataRepository;
import com.project.dropbox.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path root = Paths.get("in-memory-file-storage");  // Directory for storing files in-memory
    @Autowired
    private FileMetaDataRepository fileMetadataRepository;

    public FileStorageServiceImpl() {
        try {
            Files.createDirectories(root);  // Ensure the directory exists
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize file storage", e);
        }
    }

    @Override
    public FileMetaData uploadFile(MultipartFile file) throws IOException {
        Path filePath = this.root.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create metadata
        FileMetaData metadata = new FileMetaData();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setCreatedAt(LocalDateTime.now());
        metadata.setSize(file.getSize());
        metadata.setFileType(file.getContentType());

        return fileMetadataRepository.save(metadata);
    }

    @Override
    public Optional<FileMetaData> getFileMetadata(Long fileId) {
        return fileMetadataRepository.findById(fileId);
    }

    @Override
    public byte[] getFile(Long fileId) throws IOException {
        Optional<FileMetaData> metadata = getFileMetadata(fileId);
        if (metadata.isPresent()) {
            Path filePath = this.root.resolve(metadata.get().getFileName());
            return Files.readAllBytes(filePath);
        }
        throw new RuntimeException("File not found");
    }

    @Override
    public List<FileMetaData> listAllFiles() {
        return fileMetadataRepository.findAll();
    }

    @Override
    public void deleteFile(Long fileId) throws IOException {
        Optional<FileMetaData> metadata = getFileMetadata(fileId);
        if (metadata.isPresent()) {
            Path filePath = this.root.resolve(metadata.get().getFileName());
            Files.deleteIfExists(filePath);
            fileMetadataRepository.deleteById(fileId);
        } else {
            throw new RuntimeException("File not found");
        }
    }

    @Override
    public FileMetaData updateFile(Long fileId, MultipartFile newFile) throws IOException {
        deleteFile(fileId);  // Delete old file
        return uploadFile(newFile);  // Upload new file
    }
}
