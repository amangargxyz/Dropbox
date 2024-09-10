package com.project.dropbox.controller;

import com.project.dropbox.model.FileMetaData;
import com.project.dropbox.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<FileMetaData> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileMetaData metadata = fileStorageService.uploadFile(file);
            return new ResponseEntity<>(metadata, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long fileId) {
        try {
            byte[] fileData = fileStorageService.getFile(fileId);
            return new ResponseEntity<>(fileData, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<FileMetaData> updateFile(@PathVariable Long fileId, @RequestParam("file") MultipartFile file) {
        try {
            FileMetaData updatedMetadata = fileStorageService.updateFile(fileId, file);
            return new ResponseEntity<>(updatedMetadata, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        try {
            fileStorageService.deleteFile(fileId);
            return new ResponseEntity<>("File deleted successfully", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<FileMetaData>> listFiles() {
        List<FileMetaData> files = fileStorageService.listAllFiles();
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
