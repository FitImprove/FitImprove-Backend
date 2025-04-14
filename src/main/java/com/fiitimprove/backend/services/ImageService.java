package com.fiitimprove.backend.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fiitimprove.backend.dto.ImageFileDTO;
import com.fiitimprove.backend.dto.PubImageDTO;
import com.fiitimprove.backend.exceptions.NorPermitedAccess;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Image;
import com.fiitimprove.backend.repositories.ImageRepository;
import com.fiitimprove.backend.repositories.UserRepository;

@Service
public class ImageService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    private void validatePath(Path filePath) throws NorPermitedAccess {
        Path storagePath = Paths.get(Image.STORAGE_PATH).toAbsolutePath().normalize();
        if (!filePath.startsWith(storagePath)) 
            throw new NorPermitedAccess("Out of bound access");
    }

    public Image saveImage(Long userId, MultipartFile file) throws Exception {
        String filename = userId.toString() + "_" + (long)(Math.random() * 1000000) + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(Image.STORAGE_PATH + "\\" + filename).normalize();
        this.validatePath(filePath);

        Image img = new Image();
        img.setUser(userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        img.setPath(filename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        imageRepository.save(img);
        return img;
    }

    public byte[] getImage(String imagename) throws Exception {
        Path imagePath = Path.of(Image.STORAGE_PATH, imagename);
        this.validatePath(imagePath);
        if (Files.exists(imagePath))
            return Files.readAllBytes(imagePath);
        else throw new ResourceNotFoundException("File path is incorrect");
    }

    private List<Image> getUserImageFull(Long userId) throws Exception {
        return imageRepository.findByUserId(userId);
    }

    public List<PubImageDTO> getUserImages(Long userId) throws Exception {
        List<Image> imgs = this.getUserImageFull(userId);
        List<PubImageDTO> images = new ArrayList<>();
        for (Image img : imgs) 
            images.add(PubImageDTO.create(img));
        return images;
    }

    public List<ImageFileDTO> getUserImageFiles(Long userId) throws Exception {
        List<Image> imgs = this.getUserImageFull(userId);
        List<ImageFileDTO> arr = new ArrayList<>();
        for (Image img : imgs) {
            arr.add(new ImageFileDTO(img.getId(), this.getImage(img.getPath())));
        }
        return arr;
    }

    public void deleteImage(Long userId, Long imgId) throws Exception {
        Image img = imageRepository.findById(imgId)
            .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        if (img.getUser().getId() != userId)
            throw new RuntimeException("Cant delete that image, user is not its owner");
        
        Path path = Paths.get(Image.STORAGE_PATH + '/' + img.getPath());
        try {
            Files.delete(path);
        } catch (Exception e) {};
        imageRepository.delete(img);
    }
}
