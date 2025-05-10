package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.MentalHealthInfo;
import fr.cesi.cesizen.model.MentalHealthInfo.Category;
import fr.cesi.cesizen.repository.MentalHealthInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MentalHealthInfoService {

    private final MentalHealthInfoRepository mentalHealthInfoRepository;

    public MentalHealthInfoService(MentalHealthInfoRepository mentalHealthInfoRepository) {
        this.mentalHealthInfoRepository = mentalHealthInfoRepository;
    }

    public List<MentalHealthInfo> getAllInfo() {
        return mentalHealthInfoRepository.findAll();
    }

    public MentalHealthInfo getInfoById(Long id) {
        return mentalHealthInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Information not found with id: " + id));
    }

    public List<MentalHealthInfo> getInfoByCategory(Category category) {
        return mentalHealthInfoRepository.findByCategory(category);
    }

    public List<MentalHealthInfo> getFeaturedInfo() {
        return mentalHealthInfoRepository.findByIsFeaturedTrue();
    }

    public List<MentalHealthInfo> searchInfo(String title) {
        return mentalHealthInfoRepository.findByTitleContainingIgnoreCase(title);
    }

    public MentalHealthInfo createInfo(MentalHealthInfo info) {
        info.setCreatedAt(LocalDateTime.now());
        info.setUpdatedAt(LocalDateTime.now());
        return mentalHealthInfoRepository.save(info);
    }

    public MentalHealthInfo updateInfo(Long id, MentalHealthInfo infoDetails) {
        MentalHealthInfo info = getInfoById(id);
        
        info.setTitle(infoDetails.getTitle());
        info.setContent(infoDetails.getContent());
        info.setCategory(infoDetails.getCategory());
        info.setImageUrl(infoDetails.getImageUrl());
        info.setFeatured(infoDetails.isFeatured());
        info.setUpdatedAt(LocalDateTime.now());
        
        return mentalHealthInfoRepository.save(info);
    }

    public void deleteInfo(Long id) {
        MentalHealthInfo info = getInfoById(id);
        mentalHealthInfoRepository.delete(info);
    }

    public MentalHealthInfo toggleFeatured(Long id) {
        MentalHealthInfo info = getInfoById(id);
        info.setFeatured(!info.isFeatured());
        info.setUpdatedAt(LocalDateTime.now());
        return mentalHealthInfoRepository.save(info);
    }
} 