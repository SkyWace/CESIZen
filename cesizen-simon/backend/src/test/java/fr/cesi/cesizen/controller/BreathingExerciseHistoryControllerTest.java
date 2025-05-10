package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.dto.BreathingExerciseHistoryDto;
import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.BreathingExerciseHistory;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.payload.request.BreathingExerciseHistoryRequest;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.service.BreathingExerciseHistoryService;
import fr.cesi.cesizen.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BreathingExerciseHistoryControllerTest {

    @Mock
    private BreathingExerciseHistoryService historyService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private BreathingExerciseHistoryController historyController;

    private User testUser;
    private BreathingExercise testExercise;
    private BreathingExerciseHistory testHistory;
    private UserDetailsImpl userDetails;
    private BreathingExerciseHistoryRequest historyRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        testUser.setRoles(roles);

        testExercise = new BreathingExercise();
        testExercise.setIdExercice(1L);
        testExercise.setNom("Test Exercise");
        testExercise.setDescription("Test Description");
        testExercise.setType("Test Type");
        testExercise.setDureeInspiration(4);
        testExercise.setDureeExpiration(6);
        testExercise.setDureeApnee(2);
        testExercise.setNombreCycles(5);
        testExercise.setCreator(testUser);
        testExercise.setIsOfficial(false);

        testHistory = new BreathingExerciseHistory();
        testHistory.setId(1L);
        testHistory.setUser(testUser);
        testHistory.setExercise(testExercise);
        testHistory.setCompletedAt(LocalDateTime.now());

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", 
            "Test", "User", authorities);

        historyRequest = new BreathingExerciseHistoryRequest();
        historyRequest.setExerciseId(1L);
    }

    @Test
    void saveHistory_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(historyService.saveHistory(any(User.class), anyLong())).thenReturn(testHistory);

        ResponseEntity<BreathingExerciseHistory> response = historyController.saveHistory(historyRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testHistory, response.getBody());
        verify(historyService).saveHistory(testUser, 1L);
    }

    @Test
    void getMyHistory_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(historyService.getHistoryForUser(testUser)).thenReturn(Arrays.asList(testHistory));

        ResponseEntity<?> response = historyController.getMyHistory();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List);
        List<?> historyList = (List<?>) response.getBody();
        assertEquals(1, historyList.size());
        assertTrue(historyList.get(0) instanceof BreathingExerciseHistoryDto);
        BreathingExerciseHistoryDto dto = (BreathingExerciseHistoryDto) historyList.get(0);
        assertEquals(testHistory.getId(), dto.getId());
        assertEquals(testExercise.getIdExercice(), dto.getExerciseId());
        assertEquals(testExercise.getNom(), dto.getExerciseName());
        verify(historyService).getHistoryForUser(testUser);
    }
}