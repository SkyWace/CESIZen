package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.MentalHealthInfo;
import fr.cesi.cesizen.model.MentalHealthInfo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentalHealthInfoRepository extends JpaRepository<MentalHealthInfo, Long> {
    List<MentalHealthInfo> findByCategory(Category category);
    List<MentalHealthInfo> findByIsFeaturedTrue();
    List<MentalHealthInfo> findByTitleContainingIgnoreCase(String title);
} 