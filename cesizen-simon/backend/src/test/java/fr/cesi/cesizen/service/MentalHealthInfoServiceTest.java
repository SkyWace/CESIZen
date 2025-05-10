package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.MentalHealthInfo;
import fr.cesi.cesizen.model.MentalHealthInfo.Category;
import fr.cesi.cesizen.repository.MentalHealthInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentalHealthInfoServiceTest {

    @Mock
    private MentalHealthInfoRepository mentalHealthInfoRepository;

    @InjectMocks
    private MentalHealthInfoService mentalHealthInfoService;

    private MentalHealthInfo testInfo;

    @BeforeEach
    void setUp() {
        testInfo = new MentalHealthInfo();
        testInfo.setId(1L);
        testInfo.setTitle("Test Info");
        testInfo.setContent("Test Content");
        testInfo.setCategory(Category.ANXIETY);
        testInfo.setFeatured(false);
    }

    @Test
    void getAllInfo_ShouldReturnAllInfo() {
        List<MentalHealthInfo> infoList = Arrays.asList(testInfo);
        when(mentalHealthInfoRepository.findAll()).thenReturn(infoList);

        List<MentalHealthInfo> result = mentalHealthInfoService.getAllInfo();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mentalHealthInfoRepository).findAll();
    }

    @Test
    void getInfoById_ShouldReturnInfo_WhenInfoExists() {
        when(mentalHealthInfoRepository.findById(1L)).thenReturn(Optional.of(testInfo));

        MentalHealthInfo result = mentalHealthInfoService.getInfoById(1L);

        assertNotNull(result);
        assertEquals(testInfo.getId(), result.getId());
        assertEquals(testInfo.getTitle(), result.getTitle());
        verify(mentalHealthInfoRepository).findById(1L);
    }

    @Test
    void getInfoById_ShouldThrowException_WhenInfoDoesNotExist() {
        when(mentalHealthInfoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentalHealthInfoService.getInfoById(1L));
        verify(mentalHealthInfoRepository).findById(1L);
    }

    @Test
    void getInfoByCategory_ShouldReturnFilteredInfo() {
        List<MentalHealthInfo> infoList = Arrays.asList(testInfo);
        when(mentalHealthInfoRepository.findByCategory(Category.ANXIETY)).thenReturn(infoList);

        List<MentalHealthInfo> result = mentalHealthInfoService.getInfoByCategory(Category.ANXIETY);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Category.ANXIETY, result.get(0).getCategory());
        verify(mentalHealthInfoRepository).findByCategory(Category.ANXIETY);
    }

    @Test
    void getFeaturedInfo_ShouldReturnFeaturedInfo() {
        List<MentalHealthInfo> infoList = Arrays.asList(testInfo);
        when(mentalHealthInfoRepository.findByIsFeaturedTrue()).thenReturn(infoList);

        List<MentalHealthInfo> result = mentalHealthInfoService.getFeaturedInfo();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mentalHealthInfoRepository).findByIsFeaturedTrue();
    }

    @Test
    void searchInfo_ShouldReturnMatchingInfo() {
        List<MentalHealthInfo> infoList = Arrays.asList(testInfo);
        when(mentalHealthInfoRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(infoList);

        List<MentalHealthInfo> result = mentalHealthInfoService.searchInfo("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().contains("Test"));
        verify(mentalHealthInfoRepository).findByTitleContainingIgnoreCase("Test");
    }

    @Test
    void createInfo_ShouldCreateAndReturnInfo() {
        when(mentalHealthInfoRepository.save(any(MentalHealthInfo.class))).thenReturn(testInfo);

        MentalHealthInfo result = mentalHealthInfoService.createInfo(testInfo);

        assertNotNull(result);
        assertEquals(testInfo.getTitle(), result.getTitle());
        verify(mentalHealthInfoRepository).save(any(MentalHealthInfo.class));
    }

    @Test
    void updateInfo_ShouldUpdateAndReturnInfo() {
        MentalHealthInfo updatedInfo = new MentalHealthInfo();
        updatedInfo.setTitle("Updated Info");
        updatedInfo.setContent("Updated Content");
        updatedInfo.setCategory(Category.STRESS);
        updatedInfo.setFeatured(true);

        when(mentalHealthInfoRepository.findById(1L)).thenReturn(Optional.of(testInfo));
        when(mentalHealthInfoRepository.save(any(MentalHealthInfo.class))).thenReturn(testInfo);

        MentalHealthInfo result = mentalHealthInfoService.updateInfo(1L, updatedInfo);

        assertNotNull(result);
        assertEquals(updatedInfo.getTitle(), result.getTitle());
        assertEquals(updatedInfo.getContent(), result.getContent());
        assertEquals(updatedInfo.getCategory(), result.getCategory());
        assertEquals(updatedInfo.isFeatured(), result.isFeatured());
        verify(mentalHealthInfoRepository).findById(1L);
        verify(mentalHealthInfoRepository).save(any(MentalHealthInfo.class));
    }

    @Test
    void deleteInfo_ShouldDeleteInfo() {
        when(mentalHealthInfoRepository.findById(1L)).thenReturn(Optional.of(testInfo));
        doNothing().when(mentalHealthInfoRepository).delete(testInfo);

        mentalHealthInfoService.deleteInfo(1L);

        verify(mentalHealthInfoRepository).findById(1L);
        verify(mentalHealthInfoRepository).delete(testInfo);
    }

    @Test
    void toggleFeatured_ShouldToggleFeaturedStatus() {
        when(mentalHealthInfoRepository.findById(1L)).thenReturn(Optional.of(testInfo));
        when(mentalHealthInfoRepository.save(any(MentalHealthInfo.class))).thenReturn(testInfo);

        MentalHealthInfo result = mentalHealthInfoService.toggleFeatured(1L);

        assertNotNull(result);
        assertTrue(result.isFeatured());
        verify(mentalHealthInfoRepository).findById(1L);
        verify(mentalHealthInfoRepository).save(any(MentalHealthInfo.class));
    }
} 