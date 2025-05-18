package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.PubImageDTO;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.Image;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.ImageRepository;
import com.fiitimprove.backend.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ImageService}.
 * 
 * <p>This test class uses Mockito to mock dependencies and verifies
 * the behavior of {@code ImageService} methods related to saving,
 * retrieving, and deleting images.</p>
 */
public class ImageServiceTest {
    @InjectMocks
    private ImageService imageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests successful saving of an image for an existing user.
     * 
     * @throws Exception if saving the image fails
     */
    @Test
    public void testSaveImage_Success() throws Exception {
        Long userId = 1L;
        User user = new RegularUser();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "test content".getBytes());

        Image savedImage = imageService.saveImage(userId, file);

        assertNotNull(savedImage);
        assertEquals(user, savedImage.getUser());
        assertTrue(savedImage.getPath().contains("test.png"));
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    /**
     * Tests saving an image when the specified user does not exist,
     * expecting a {@link ResourceNotFoundException} to be thrown.
     */
    @Test
    public void testSaveImage_UserNotFound() {
        Long userId = 100L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "test content".getBytes());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            imageService.saveImage(userId, file);
        });

        assertEquals("User not found", exception.getMessage());
    }

    /**
     * Tests retrieving all images associated with a specific user,
     * expecting a non-empty list of image DTOs.
     * 
     * @throws Exception if retrieval fails
     */
    @Test
    public void testGetUserImages_Success() throws Exception {
        Long userId = 1L;
        Image image = new Image();
        image.setId(1L);
        image.setPath("img.png");
        image.setUser(new RegularUser());

        when(imageRepository.findByUserId(userId)).thenReturn(List.of(image));

        List<PubImageDTO> images = imageService.getUserImages(userId);
        assertEquals(1, images.size());
        assertEquals("img.png", images.get(0).getPath());
    }

    /**
     * Tests deleting an image that does not belong to the specified user,
     * expecting an exception indicating the image cannot be deleted.
     */
    @Test
    public void testDeleteImage_NotOwner() {
        Long userId = 1L;
        Image image = new Image();
        User anotherUser = new RegularUser();
        anotherUser.setId(2L);
        image.setUser(anotherUser);
        image.setId(1L);
        image.setPath("file.png");

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            imageService.deleteImage(userId, 1L);
        });

        assertTrue(exception.getMessage().contains("Cant delete that image"));
    }

    /**
     * Tests successful deletion of an image by its owner,
     * including verification that the image file is removed from storage
     * and the image record is deleted from the repository.
     * 
     * @throws Exception if deletion fails
     */
    @Test
    public void testDeleteImage_Success() throws Exception {
        Long userId = 1L;
        Image image = new Image();
        image.setId(1L);
        image.setPath("to_delete.png");
        User user = new RegularUser();
        user.setId(userId);
        image.setUser(user);

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        Path imagePath = Paths.get(Image.STORAGE_PATH + "/to_delete.png");
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, "test".getBytes());

        imageService.deleteImage(userId, 1L);

        assertFalse(Files.exists(imagePath));
        verify(imageRepository, times(1)).delete(image);
    }
}
