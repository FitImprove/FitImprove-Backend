package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.IncorrectDataException;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
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
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TrainingUserService}.
 * <p>
 * Tests cover user enrollment, cancellation, invitation acceptance/denial,
 * creation validations, and retrieval of enrolled trainings.
 * <p>
 */
class TrainingUserServiceTest {
    @InjectMocks
    private TrainingUserService trainingUserService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private TrainingUserRepository trainingUserRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private RegularUserRepository regularUserRepository;
    @Mock
    private CoachRepository coachRepository;

    private Training training;
    private RegularUser user;

    /**
     * Sets up mock and create training, coach and user for each test to use
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Coach c = new Coach();
        c.setId(11L);

        training = new Training();
        training.setId(1L);
        training.setCoach(c);
        training.setFreeSlots(5);
        training.setCanceled(false);
        training.setForType(Training.ForType.EVERYONE);
        training.setType("SomeType");
        training.setDurationMinutes(15);
        training.setTitle("Title");
        training.setDescription("Some description");
        training.setTime(LocalDateTime.now().plusHours(1));

        user = new RegularUser();
        user.setId(10L);
    }

    /**
     * Tests successful enrollment of a user into a training.
     * Verifies training user status, free slots decrement,
     * and that the repository save method is called.
     */
    @Test
    public void testEnrollUserInTraining_Success() throws Exception {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(regularUserRepository.findById(10L)).thenReturn(Optional.of(user));
        when(trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(Collections.emptyList());
        when(trainingUserRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        TrainingUser result = trainingUserService.enrollUserInTraining(1L, 10L);

        assertNotNull(result);
        assertEquals(TrainingUser.Status.AGREED, result.getStatus());
        verify(trainingUserRepository, times(1)).save(result);
        assertEquals(4, training.getFreeSlots());
    }

    /**
     * Tests that enrolling a user throws {@link ResourceNotFoundException}
     * when the training is not found.
     */
    @Test
    public void testEnrollUserInTraining_TrainingNotFound() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
            trainingUserService.enrollUserInTraining(1L, 10L)
        );
        assertEquals("Training not found", ex.getMessage());
    }

    /**
     * Tests that enrolling a user throws {@link ResourceNotFoundException}
     * when the user is not found.
     */
    @Test
    public void testEnrollUserInTraining_UserNotFound() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(regularUserRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
            trainingUserService.enrollUserInTraining(1L, 10L)
        );
        assertEquals("User not found", ex.getMessage());
    }

    /**
     * Tests successful cancellation of a training enrollment.
     * Verifies status update, cancellation timestamp, and free slots increment.
     */
    @Test
    public void testCancelTraining_Success() throws Exception {
        TrainingUser tu = new TrainingUser();
        tu.setStatus(TrainingUser.Status.AGREED);
        tu.setTraining(training);

        when(trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(1L, 10L, List.of(TrainingUser.Status.AGREED))).thenReturn(List.of(tu));
        when(trainingRepository.save(training)).thenReturn(training);
        when(trainingUserRepository.save(tu)).thenReturn(tu);

        TrainingUser canceled = trainingUserService.cancelTraining(1L, 10L);

        assertEquals(TrainingUser.Status.CANCELED, canceled.getStatus());
        assertNotNull(canceled.getCanceledAt());
        assertEquals(6, training.getFreeSlots()); // Free slots incremented
    }

    /**
     * Tests that cancelling a training throws {@link ResourceNotFoundException}
     * when the user does not have a reservation.
     */
    @Test
    public void testCancelTraining_NoReservation() {
        when(trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(Collections.emptyList());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
            trainingUserService.cancelTraining(1L, 10L)
        );
        assertEquals("User does not have an reservation in provided training", ex.getMessage());
    }

    /**
     * Tests successful denial of a training invitation.
     * Verifies status update and repository save calls.
     */
    @Test
    public void testDenyInvitation_Success() throws Exception {
        TrainingUser tu = new TrainingUser();
        tu.setStatus(TrainingUser.Status.INVITED);
        tu.setTraining(training);

        when(trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(1L, 10L, List.of(TrainingUser.Status.INVITED))).thenReturn(List.of(tu));
        when(trainingRepository.save(training)).thenReturn(training);
        when(trainingUserRepository.save(tu)).thenReturn(tu);

        TrainingUser denied = trainingUserService.denyInvitation(1L, 10L);

        assertEquals(TrainingUser.Status.DENIED, denied.getStatus());
        verify(trainingRepository, times(1)).save(training);
    }

    /**
     * Tests that denying an invitation throws {@link IncorrectDataException}
     * when the user does not have an invitation.
     */
    @Test
    public void testDenyInvitation_NoInvitation() {
        when(trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(Collections.emptyList());

        IncorrectDataException ex = assertThrows(IncorrectDataException.class, () ->
            trainingUserService.denyInvitation(1L, 10L)
        );
        assertEquals("User does not have an invitation in provided training", ex.getMessage());
    }

    /**
     * Tests successful acceptance of a training invitation.
     * Verifies status update and booking timestamp.
     */
    @Test
    public void testAcceptInvitation_Success() throws Exception {
        TrainingUser tu = new TrainingUser();
        tu.setStatus(TrainingUser.Status.INVITED);
        tu.setTraining(training);

        when(trainingUserRepository.
            findByTrainingIdAndUserIdAndStatusIn(1L, 10L, List.of(TrainingUser.Status.INVITED)))
            .thenReturn(List.of(tu));
        when(trainingUserRepository.save(tu)).thenReturn(tu);

        TrainingUser accepted = trainingUserService.acceptInvitation(1L, 10L);

        assertEquals(TrainingUser.Status.AGREED, accepted.getStatus());
        assertNotNull(accepted.getBookedAt());
    }

    /**
     * Tests that accepting an invitation throws {@link IncorrectDataException}
     * when the user does not have an invitation.
     */
    @Test
    public void testAcceptInvitation_NoInvitation() {
        when(trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(Collections.emptyList());

        IncorrectDataException ex = assertThrows(IncorrectDataException.class, () ->
            trainingUserService.acceptInvitation(1L, 10L)
        );
        assertEquals("User does not have an invitation in provided training", ex.getMessage());
    }

    /**
     * Tests that creating a TrainingUser with invalid status throws {@link IncorrectDataException}.
     */
    @Test
    public void testCreate_InvalidStatus() {
        IncorrectDataException ex = assertThrows(IncorrectDataException.class, () -> 
            trainingUserService.create(training, user, TrainingUser.Status.CANCELED)
        );
        assertTrue(ex.getMessage().contains("Can not create training that is not INVITED or AGREED"));
    }

    /**
     * Tests that TrainingUser create wont create a connection for canceled training
     */
    @Test
    public void testCreate_TrainingCanceled() {
        training.setCanceled(true);
        IncorrectDataException ex = assertThrows(IncorrectDataException.class, () -> 
            trainingUserService.create(training, user, TrainingUser.Status.AGREED)
        );
        assertEquals("Cannot enroll in a canceled training", ex.getMessage());
    }

    /**
     * Tests that TrainingUser wont be created for a training that does not have free slots left
     */
    @Test
    public void testCreate_NoFreeSlots() {
        training.setFreeSlots(0);
        IncorrectDataException ex = assertThrows(IncorrectDataException.class, () -> 
            trainingUserService.create(training, user, TrainingUser.Status.AGREED)
        );
        assertEquals("No free slots available", ex.getMessage());
    }

    /**
     * Test verifies that TrainingUser can not be created for the training that already happened
     */
    @Test
    public void testCreate_TrainingInPast() {
        training.setTime(LocalDateTime.now().minusMinutes(1));
        IncorrectDataException ex = assertThrows(IncorrectDataException.class, () -> 
            trainingUserService.create(training, user, TrainingUser.Status.AGREED)
        );
        assertEquals("Can not enroll in training that already started/ended", ex.getMessage());
    }

    /**
     * Test verifies that TrainingUser wont be create if it already exists for this pair of training and user
     * @throws Exception
     */
    @Test
    public void testCreate_UserAlreadyEnrolled() throws Exception {
        TrainingUser existingTU = new TrainingUser();
        existingTU.setStatus(TrainingUser.Status.AGREED);

        when(trainingUserRepository.findByTrainingIdAndUserIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(List.of(existingTU));

        IncorrectDataException ex = assertThrows(IncorrectDataException.class, () -> 
            trainingUserService.create(training, user, TrainingUser.Status.AGREED)
        );
        assertEquals("User is already enrolled in this training", ex.getMessage());
    }

    /**
     * Checks weather "getAllEntoledTrainings" returns correct list of trainings
     */
    @Test
    public void testGetAllEntoledTrainings() {
        List<TrainingUser> list = List.of(new TrainingUser(), new TrainingUser());
        when(trainingUserRepository.findByUserId(10L)).thenReturn(list);

        List<TrainingUser> result = trainingUserService.getAllEntoledTrainings(10L);

        assertEquals(2, result.size());
    }
}
