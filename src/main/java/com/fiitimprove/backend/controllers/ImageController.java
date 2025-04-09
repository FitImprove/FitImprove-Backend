package com.fiitimprove.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fiitimprove.backend.dto.ImageFileDTO;
import com.fiitimprove.backend.dto.PubImageDTO;
import com.fiitimprove.backend.models.Image;
import com.fiitimprove.backend.services.ImageService;


@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired
    private ImageService imgService;

    @GetMapping("/descriptors/{userId}")
    public ResponseEntity<List<PubImageDTO>> getImageDescriptors(@PathVariable Long userId) throws Exception {
        return ResponseEntity.ok(imgService.getUserImages(userId));
    }

    @GetMapping("/files/{userId}")
    public ResponseEntity<List<ImageFileDTO>> getImages(@PathVariable Long userId) throws Exception {
        return ResponseEntity.ok(
            imgService.getUserImageFiles(userId));
    }

    @GetMapping("/get/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws Exception {
        return ResponseEntity.ok( 
            imgService.getImage(filename) );
    }

    @PostMapping("/upload/{userId}")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) throws Exception {
        Image saved = imgService.saveImage(userId, file);
        return ResponseEntity.ok(PubImageDTO.create(saved));
    }

    @DeleteMapping("/del/{userId}/{imgId}")
    public ResponseEntity<?> deleteImage(@PathVariable("userId") Long userId, 
            @PathVariable("imgId") Long imgId) throws Exception {
        imgService.deleteImage(userId, imgId);
        return ResponseEntity.ok(null);
    }
}
