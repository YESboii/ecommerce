package com.ayush.ayush.service;

import com.ayush.ayush.exceptions.StorageException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public  class FileServiceImpl implements FileService{
//    @Value("${file.path}")
    private static final String path = "images/products";

    private Path basePath;

    public FileServiceImpl(){

    }
    @PostConstruct
    @Override
    public void init() {
        System.out.println("hello");
        System.out.println("inside constructor!!!");
        if(path.trim().length() == 0){
            throw new StorageException("File upload location can not be Empty.");
        }
        this.basePath = Paths.get(path);
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new StorageException("Could not create the base directories");
        }
    }
    @Override
    public Path loadPathToFile(String fileName) {
        //create a clean method..
        return basePath.resolve(fileName).normalize();
    }

    @Override
    public byte[] loadFileAsBytes(String filename) {
        if(filename==null) return null;
        Path filePath = loadPathToFile(filename);
        byte []image;
        try {
            image = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new StorageException("Error reading file:%s".formatted(filename),e);
        }
        return image;
    }


    @Override
    public String save(MultipartFile file) {
        String filename = null;
        try {
            if(file.isEmpty() || file == null){
                throw new StorageException("Empty File Cannot be Stored. Filename: %s".formatted(file.getOriginalFilename()));
            }
            filename = getUniqueName(file);
            Path destinationPath = loadPathToFile(filename);
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream,destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

        }catch (IOException e){
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
        return filename;
    }

    @Override
    public void update(MultipartFile file,String fileName) {
        try {
            if(file.isEmpty() || file == null){
                throw new StorageException("Empty File Cannot be Stored. Filename: %s".formatted(file.getOriginalFilename()));
            }
            Path destinationPath = loadPathToFile(fileName);
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream,destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

        }catch (IOException e){
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void delete(String filename) {
        Path filePath = loadPathToFile(filename);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new StorageException("Error removing File: %s".formatted(filename),e);
        }
    }

    private String getUniqueName(MultipartFile file){
        return UUID.randomUUID().toString()+"." + StringUtils.getFilenameExtension(file.getOriginalFilename());
    }

}
