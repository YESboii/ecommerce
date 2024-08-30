package com.ayush.ayush.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileService {
    void init();


    Path loadPathToFile(String fileName);

    byte[] loadFileAsBytes(String fileName);

    String save(MultipartFile file);

    void update(MultipartFile file,String filename);

    void delete(String filename);
}
