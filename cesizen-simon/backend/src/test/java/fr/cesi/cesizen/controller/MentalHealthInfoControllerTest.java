package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.MentalHealthInfo;
import fr.cesi.cesizen.model.MentalHealthInfo.Category;
import fr.cesi.cesizen.service.MentalHealthInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentalHealthInfoControllerTest {

    @Mock
    private MentalHealthInfoService mentalHealthInfoService;

    @InjectMocks
    private MentalHealthInfoController mentalHealthInfoController;

    private MentalHealthInfo testInfo;

    @BeforeEach
    void setUp() {
        testInfo = new MentalHealthInfo();
        testInfo.setId(1L);
        testInfo.setTitle("Test Title");
        testInfo.setContent("Test Content");
        testInfo.setCategory(Category.ANXIETY);
        testInfo.setImageUrl("test-image.jpg");
        testInfo.setCreatedAt(LocalDateTime.now());
        testInfo.setUpdatedAt(LocalDateTime.now());
        testInfo.setFeatured(false);
    }

    @Test
    void getAllInfo_Success() {
        List<MentalHealthInfo> infoList = Arrays.asList(testInfo);
        when(mentalHealthInfoService.getAllInfo()).thenReturn(infoList);

        ResponseEntity<List<MentalHealthInfo>> response = mentalHealthInfoController.getAllInfo();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(infoList, response.getBody());
        verify(mentalHealthInfoService).getAllInfo();
    }

    @Test
    void getInfoById_Success() {
        when(mentalHealthInfoService.getInfoById(1L)).thenReturn(testInfo);

        ResponseEntity<MentalHealthInfo> response = mentalHealthInfoController.getInfoById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testInfo, response.getBody());
        verify(mentalHealthInfoService).getInfoById(1L);
    }

    @Test
    void getInfoByCategory_Success() {
        List<MentalHealthInfo> infoList = Arrays.asList(testInfo);
        when(mentalHealthInfoService.getInfoByCategory(Category.ANXIETY)).thenReturn(infoList);

        ResponseEntity<List<MentalHealthInfo>> response = mentalHealthInfoController.getInfoByCategory(Category.ANXIETY);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(infoList, response.getBody());
        verify(mentalHealthInfoService).getInfoByCategory(Category.ANXIETY);
    }

    @Test
    void getFeaturedInfo_Success() {
        List<MentalHealthInfo> infoList = Arrays.asList(testInfo);
        when(mentalHealthInfoService.getFeaturedInfo()).thenReturn(infoList);

        ResponseEntity<List<MentalHealthInfo>> response = mentalHealthInfoController.getFeaturedInfo();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(infoList, response.getBody());
        verify(mentalHealthInfoService).getFeaturedInfo();
    }

    @Test
    void searchInfo_Success() {
        List<MentalHealthInfo> infoList = Arrays.asList(testInfo);
        when(mentalHealthInfoService.searchInfo("Test")).thenReturn(infoList);

        ResponseEntity<List<MentalHealthInfo>> response = mentalHealthInfoController.searchInfo("Test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(infoList, response.getBody());
        verify(mentalHealthInfoService).searchInfo("Test");
    }

    @Test
    void createInfo_Success() {
        when(mentalHealthInfoService.createInfo(any(MentalHealthInfo.class))).thenReturn(testInfo);

        ResponseEntity<MentalHealthInfo> response = mentalHealthInfoController.createInfo(testInfo);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testInfo, response.getBody());
        verify(mentalHealthInfoService).createInfo(testInfo);
    }

    @Test
    void updateInfo_Success() {
        when(mentalHealthInfoService.updateInfo(anyLong(), any(MentalHealthInfo.class))).thenReturn(testInfo);

        ResponseEntity<MentalHealthInfo> response = mentalHealthInfoController.updateInfo(1L, testInfo);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testInfo, response.getBody());
        verify(mentalHealthInfoService).updateInfo(1L, testInfo);
    }

    @Test
    void deleteInfo_Success() {
        doNothing().when(mentalHealthInfoService).deleteInfo(1L);

        ResponseEntity<Void> response = mentalHealthInfoController.deleteInfo(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(mentalHealthInfoService).deleteInfo(1L);
    }

    @Test
    void toggleFeatured_Success() {
        when(mentalHealthInfoService.toggleFeatured(1L)).thenReturn(testInfo);

        ResponseEntity<MentalHealthInfo> response = mentalHealthInfoController.toggleFeatured(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testInfo, response.getBody());
        verify(mentalHealthInfoService).toggleFeatured(1L);
    }
}