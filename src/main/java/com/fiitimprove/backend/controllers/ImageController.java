package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.ImageFileDTO;
import com.fiitimprove.backend.dto.PubImageDTO;
import com.fiitimprove.backend.models.Image;
import com.fiitimprove.backend.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imgService;

    public ImageController(ImageService imgService) {
        this.imgService = imgService;
    }

    @GetMapping("/descriptors/{userId}")
    @Operation(summary = "Get image descriptors for a user", description = "Retrieves image descriptors for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image descriptors retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<PubImageDTO>> getImageDescriptors(@PathVariable Long userId) throws Exception {
        return ResponseEntity.ok(imgService.getUserImages(userId));
    }

    @GetMapping("/files/{userId}")
    @Operation(summary = "Get image files for a user", description = "Retrieves image files for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image files retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<ImageFileDTO>> getImages(@PathVariable Long userId) throws Exception {
        return ResponseEntity.ok(imgService.getUserImageFiles(userId));
    }

    @GetMapping("/get/{filename}")
    @Operation(summary = "Get an image by filename", description = "Retrieves an image by its filename")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws Exception {
        return ResponseEntity.ok(imgService.getImage(filename));
    }

    @PostMapping("/upload/{userId}")
    @Operation(summary = "Upload an image for a user", description = "Uploads an image for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> uploadImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) throws Exception {
        Image saved = imgService.saveImage(userId, file);
        return ResponseEntity.ok(PubImageDTO.create(saved));
    }

    @DeleteMapping("/del/{userId}/{imgId}")
    @Operation(summary = "Delete an image", description = "Deletes an image for a specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User or image not found")
    })
    public ResponseEntity<?> deleteImage(@PathVariable("userId") Long userId,
                                         @PathVariable("imgId") Long imgId) throws Exception {
        imgService.deleteImage(userId, imgId);
        return ResponseEntity.ok(null);
    }
}