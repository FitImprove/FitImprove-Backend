package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.TrainingEditDTO;
import com.fiitimprove.backend.exceptions.AlreadyClosedException;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.Training;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.repositories.CoachRepository;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import com.fiitimprove.backend.repositories.TrainingRepository;
import com.fiitimprove.backend.repositories.TrainingUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TrainingService}.
 * 
 * <p>This class tests various operations related to training management,
 * including creation, cancellation, and editing of trainings.</p>
 */
public class TrainingServiceTest {
    @InjectMocks
    private TrainingService trainingService;
    @Mock
    private TrainingUserService trainingUserService;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private CoachRepository coachRepository;
    @Mock
    private RegularUserRepository regularUserRepository;
    @Mock
    private TrainingUserRepository trainingUserRepository;

    /**
     * Initializes Mockito mocks before each test.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests that creating a training scheduled less than 15 minutes
     * from now throws an {@link AlreadyClosedException}.
     */
    @Test
    public void testCreateTraining_throwsAlreadyClosedException_whenTrainingTimeTooSoon() {
        Training training = new Training();
        training.setTime(LocalDateTime.now().plusMinutes(10)); // less than 15 minutes from now
        training.setForType(Training.ForType.EVERYONE);

        Coach coach = new Coach();
        coach.setId(1L);
        when(coachRepository.findById(1L)).thenReturn(Optional.of(coach));
        Exception exception = assertThrows(AlreadyClosedException.class, () -> {
            trainingService.createTraining(coach.getId(), training, null);
        });
        assertTrue(exception.getMessage().contains("at least 15 minutes"));
    }

    /**
     * Tests successful creation of a training with a valid future time
     * and invited users.
     * Verifies the coach association and creation of invitations for users.
     * 
     * @throws Exception if creation fails unexpectedly
     */
    @Test
    public void testCreateTraining_success() throws Exception {
        Coach coach = new Coach();
        coach.setId(1L);

        Training training = new Training();
        LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
        training.setTime(futureTime);

        when(coachRepository.findById(1L)).thenReturn(Optional.of(coach));
        when(trainingRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(regularUserRepository.findById(anyLong())).thenReturn(Optional.of(new RegularUser()));

        List<Long> invitedUserIds = List.of(2L, 3L);

        Training result = trainingService.createTraining(1L, training, invitedUserIds);

        assertEquals(coach, result.getCoach());
        verify(trainingUserService, times(invitedUserIds.size()))
            .createUnsafe(any(), any(), eq(TrainingUser.Status.INVITED));
    }

    /**
     * Tests that attempting to cancel an already canceled training
     * throws an {@link AlreadyClosedException}.
     */
    @Test
    public void testCancel_trainingAlreadyCanceled_throws() {
        Training training = new Training();
        training.setCanceled(true);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Exception ex = assertThrows(AlreadyClosedException.class, () -> {
            trainingService.cancel(1L);
        });

        assertEquals("Training is already canceled", ex.getMessage());
    }

    /**
     * Tests successful cancellation of a training.
     * Verifies that the training is marked canceled and
     * all associated users' statuses are updated accordingly.
     * 
     * @throws Exception if cancellation fails unexpectedly
     */
    @Test
    public void testCancel_success() throws Exception {
        TrainingUser user1 = mock(TrainingUser.class);
        when(user1.getStatus()).thenReturn(TrainingUser.Status.AGREED);

        TrainingUser user2 = mock(TrainingUser.class);
        when(user2.getStatus()).thenReturn(TrainingUser.Status.INVITED);

        List<TrainingUser> users = List.of(user1, user2);

        Training training = new Training();
        training.setCanceled(false);
        training.setTrainingUsers(users);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainingRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(trainingUserRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        Training canceled = trainingService.cancel(1L);

        assertTrue(canceled.isCanceled());
        verify(user1).setCanceledAt(any());
        verify(user1).setStatus(TrainingUser.Status.CANCELED);

        verify(user2).setCanceledAt(any());
        verify(user2).setStatus(TrainingUser.Status.CANCELED);
    }

    /**
     * Tests that editing a training with a negative number of free slots
     * throws a {@link RuntimeException}.
     */
    @Test
    public void testEdit_throwsException_whenFreeSlotsNegative() {
        TrainingEditDTO dto = new TrainingEditDTO();
        dto.setFreeSlots(-1);

        Exception ex = assertThrows(RuntimeException.class, () -> trainingService.edit(dto));
        assertTrue(ex.getMessage().contains("freeSlots field"));
    }

    /**
     * Tests successful editing of a training's details.
     * Verifies that the training is updated with the new values provided
     * in the {@link TrainingEditDTO}.
     */
    @Test
    public void testEdit_success() {
        TrainingEditDTO dto = new TrainingEditDTO();
        dto.setId(1L);
        dto.setTitle("New Title");
        dto.setDescription("Desc");
        dto.setFreeSlots(5);
        dto.setForType(Training.ForType.EVERYONE);
        dto.setType("TypeB");

        Training existing = new Training();
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(trainingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Training edited = trainingService.edit(dto);

        assertEquals("New Title", edited.getTitle());
        assertEquals("Desc", edited.getDescription());
        assertEquals(5, edited.getFreeSlots());
        assertEquals(Training.ForType.EVERYONE, edited.getForType());
        assertEquals("TypeB", edited.getType());
    }
}
