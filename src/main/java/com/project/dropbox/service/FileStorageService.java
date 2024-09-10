package com.project.dropbox.service;

import com.project.dropbox.model.FileMetaData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FileStorageService {
    public FileMetaData uploadFile(MultipartFile file) throws IOException;
    public Optional<FileMetaData> getFileMetadata(Long fileId);
    public byte[] getFile(Long fileId) throws IOException;
    public List<FileMetaData> listAllFiles();
    public void deleteFile(Long fileId) throws IOException;
    public FileMetaData updateFile(Long fileId, MultipartFile newFile) throws IOException;
}
