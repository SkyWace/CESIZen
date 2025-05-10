package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.MentalHealthInfo;
import fr.cesi.cesizen.model.MentalHealthInfo.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class MentalHealthInfoRepositoryTest {

    @Autowired
    private MentalHealthInfoRepository mentalHealthInfoRepository;

    @BeforeEach
    public void setUp() {
        // Clear the repository before each test
        mentalHealthInfoRepository.deleteAll();
    }

    @Test
    public void testSaveMentalHealthInfo() {
        // Create a new mental health info
        MentalHealthInfo info = new MentalHealthInfo();
        info.setTitle("Managing Anxiety");
        info.setContent("Tips for managing anxiety...");
        info.setCategory(Category.ANXIETY);
        info.setImageUrl("anxiety.jpg");
        info.setFeatured(false);

        // Save the info
        MentalHealthInfo savedInfo = mentalHealthInfoRepository.save(info);

        // Verify the info was saved
        assertThat(savedInfo).isNotNull();
        assertThat(savedInfo.getId()).isNotNull();
        assertThat(savedInfo.getTitle()).isEqualTo("Managing Anxiety");
        assertThat(savedInfo.getContent()).isEqualTo("Tips for managing anxiety...");
        assertThat(savedInfo.getCategory()).isEqualTo(Category.ANXIETY);
        assertThat(savedInfo.getImageUrl()).isEqualTo("anxiety.jpg");
        assertThat(savedInfo.isFeatured()).isFalse();
        assertThat(savedInfo.getCreatedAt()).isNotNull();
        assertThat(savedInfo.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testFindMentalHealthInfoById() {
        // Create and save a new mental health info
        MentalHealthInfo info = new MentalHealthInfo();
        info.setTitle("Managing Anxiety");
        info.setContent("Tips for managing anxiety...");
        info.setCategory(Category.ANXIETY);
        info.setImageUrl("anxiety.jpg");
        info.setFeatured(false);
        MentalHealthInfo savedInfo = mentalHealthInfoRepository.save(info);

        // Find the info by ID
        Optional<MentalHealthInfo> foundInfo = mentalHealthInfoRepository.findById(savedInfo.getId());

        // Verify the info was found
        assertThat(foundInfo).isPresent();
        assertThat(foundInfo.get().getId()).isEqualTo(savedInfo.getId());
        assertThat(foundInfo.get().getTitle()).isEqualTo("Managing Anxiety");
        assertThat(foundInfo.get().getCategory()).isEqualTo(Category.ANXIETY);
    }

    @Test
    public void testUpdateMentalHealthInfo() {
        // Create and save a new mental health info
        MentalHealthInfo info = new MentalHealthInfo();
        info.setTitle("Managing Anxiety");
        info.setContent("Tips for managing anxiety...");
        info.setCategory(Category.ANXIETY);
        info.setImageUrl("anxiety.jpg");
        info.setFeatured(false);
        MentalHealthInfo savedInfo = mentalHealthInfoRepository.save(info);

        // Update the info
        savedInfo.setTitle("Updated Anxiety Management");
        savedInfo.setContent("Updated tips for managing anxiety...");
        savedInfo.setFeatured(true);
        MentalHealthInfo updatedInfo = mentalHealthInfoRepository.save(savedInfo);

        // Verify the info was updated
        assertThat(updatedInfo.getId()).isEqualTo(savedInfo.getId());
        assertThat(updatedInfo.getTitle()).isEqualTo("Updated Anxiety Management");
        assertThat(updatedInfo.getContent()).isEqualTo("Updated tips for managing anxiety...");
        assertThat(updatedInfo.isFeatured()).isTrue();
    }

    @Test
    public void testDeleteMentalHealthInfo() {
        // Create and save a new mental health info
        MentalHealthInfo info = new MentalHealthInfo();
        info.setTitle("Managing Anxiety");
        info.setContent("Tips for managing anxiety...");
        info.setCategory(Category.ANXIETY);
        info.setImageUrl("anxiety.jpg");
        info.setFeatured(false);
        MentalHealthInfo savedInfo = mentalHealthInfoRepository.save(info);

        // Delete the info
        mentalHealthInfoRepository.delete(savedInfo);

        // Verify the info was deleted
        Optional<MentalHealthInfo> deletedInfo = mentalHealthInfoRepository.findById(savedInfo.getId());
        assertThat(deletedInfo).isEmpty();
    }

    @Test
    public void testFindByCategory() {
        // Create and save mental health info with different categories
        MentalHealthInfo anxietyInfo = new MentalHealthInfo();
        anxietyInfo.setTitle("Managing Anxiety");
        anxietyInfo.setContent("Tips for managing anxiety...");
        anxietyInfo.setCategory(Category.ANXIETY);
        anxietyInfo.setImageUrl("anxiety.jpg");
        anxietyInfo.setFeatured(false);
        mentalHealthInfoRepository.save(anxietyInfo);

        MentalHealthInfo depressionInfo = new MentalHealthInfo();
        depressionInfo.setTitle("Coping with Depression");
        depressionInfo.setContent("Tips for coping with depression...");
        depressionInfo.setCategory(Category.DEPRESSION);
        depressionInfo.setImageUrl("depression.jpg");
        depressionInfo.setFeatured(false);
        mentalHealthInfoRepository.save(depressionInfo);

        MentalHealthInfo stressInfo = new MentalHealthInfo();
        stressInfo.setTitle("Stress Relief");
        stressInfo.setContent("Tips for stress relief...");
        stressInfo.setCategory(Category.STRESS);
        stressInfo.setImageUrl("stress.jpg");
        stressInfo.setFeatured(false);
        mentalHealthInfoRepository.save(stressInfo);

        // Find info by category
        List<MentalHealthInfo> anxietyInfos = mentalHealthInfoRepository.findByCategory(Category.ANXIETY);
        List<MentalHealthInfo> depressionInfos = mentalHealthInfoRepository.findByCategory(Category.DEPRESSION);
        List<MentalHealthInfo> stressInfos = mentalHealthInfoRepository.findByCategory(Category.STRESS);
        List<MentalHealthInfo> mindfulnessInfos = mentalHealthInfoRepository.findByCategory(Category.MINDFULNESS);

        // Verify info was found by category
        assertThat(anxietyInfos).hasSize(1);
        assertThat(anxietyInfos.get(0).getTitle()).isEqualTo("Managing Anxiety");
        assertThat(anxietyInfos.get(0).getCategory()).isEqualTo(Category.ANXIETY);

        assertThat(depressionInfos).hasSize(1);
        assertThat(depressionInfos.get(0).getTitle()).isEqualTo("Coping with Depression");
        assertThat(depressionInfos.get(0).getCategory()).isEqualTo(Category.DEPRESSION);

        assertThat(stressInfos).hasSize(1);
        assertThat(stressInfos.get(0).getTitle()).isEqualTo("Stress Relief");
        assertThat(stressInfos.get(0).getCategory()).isEqualTo(Category.STRESS);

        assertThat(mindfulnessInfos).isEmpty();
    }

    @Test
    public void testFindByIsFeaturedTrue() {
        // Create and save featured and non-featured mental health info
        MentalHealthInfo featuredInfo1 = new MentalHealthInfo();
        featuredInfo1.setTitle("Featured Anxiety Management");
        featuredInfo1.setContent("Featured tips for managing anxiety...");
        featuredInfo1.setCategory(Category.ANXIETY);
        featuredInfo1.setImageUrl("featured-anxiety.jpg");
        featuredInfo1.setFeatured(true);
        mentalHealthInfoRepository.save(featuredInfo1);

        MentalHealthInfo featuredInfo2 = new MentalHealthInfo();
        featuredInfo2.setTitle("Featured Depression Coping");
        featuredInfo2.setContent("Featured tips for coping with depression...");
        featuredInfo2.setCategory(Category.DEPRESSION);
        featuredInfo2.setImageUrl("featured-depression.jpg");
        featuredInfo2.setFeatured(true);
        mentalHealthInfoRepository.save(featuredInfo2);

        MentalHealthInfo nonFeaturedInfo = new MentalHealthInfo();
        nonFeaturedInfo.setTitle("Non-Featured Stress Relief");
        nonFeaturedInfo.setContent("Non-featured tips for stress relief...");
        nonFeaturedInfo.setCategory(Category.STRESS);
        nonFeaturedInfo.setImageUrl("non-featured-stress.jpg");
        nonFeaturedInfo.setFeatured(false);
        mentalHealthInfoRepository.save(nonFeaturedInfo);

        // Find featured info
        List<MentalHealthInfo> featuredInfos = mentalHealthInfoRepository.findByIsFeaturedTrue();

        // Verify only featured info was found
        assertThat(featuredInfos).hasSize(2);
        assertThat(featuredInfos).extracting(MentalHealthInfo::getTitle)
            .containsExactlyInAnyOrder("Featured Anxiety Management", "Featured Depression Coping");
        assertThat(featuredInfos).extracting(MentalHealthInfo::isFeatured)
            .containsOnly(true);
    }

    @Test
    public void testFindByTitleContainingIgnoreCase() {
        // Create and save mental health info with different titles
        MentalHealthInfo anxietyInfo = new MentalHealthInfo();
        anxietyInfo.setTitle("Managing Anxiety");
        anxietyInfo.setContent("Tips for managing anxiety...");
        anxietyInfo.setCategory(Category.ANXIETY);
        anxietyInfo.setImageUrl("anxiety.jpg");
        anxietyInfo.setFeatured(false);
        mentalHealthInfoRepository.save(anxietyInfo);

        MentalHealthInfo depressionInfo = new MentalHealthInfo();
        depressionInfo.setTitle("Coping with Depression");
        depressionInfo.setContent("Tips for coping with depression...");
        depressionInfo.setCategory(Category.DEPRESSION);
        depressionInfo.setImageUrl("depression.jpg");
        depressionInfo.setFeatured(false);
        mentalHealthInfoRepository.save(depressionInfo);

        MentalHealthInfo stressInfo = new MentalHealthInfo();
        stressInfo.setTitle("Stress Relief Techniques");
        stressInfo.setContent("Tips for stress relief...");
        stressInfo.setCategory(Category.STRESS);
        stressInfo.setImageUrl("stress.jpg");
        stressInfo.setFeatured(false);
        mentalHealthInfoRepository.save(stressInfo);

        // Find info by title containing (case-insensitive)
        List<MentalHealthInfo> managingInfos = mentalHealthInfoRepository.findByTitleContainingIgnoreCase("managing");
        List<MentalHealthInfo> copingInfos = mentalHealthInfoRepository.findByTitleContainingIgnoreCase("coping");
        List<MentalHealthInfo> reliefInfos = mentalHealthInfoRepository.findByTitleContainingIgnoreCase("relief");
        List<MentalHealthInfo> techniqueInfos = mentalHealthInfoRepository.findByTitleContainingIgnoreCase("technique");
        List<MentalHealthInfo> lowerCaseInfos = mentalHealthInfoRepository.findByTitleContainingIgnoreCase("anxiety");
        List<MentalHealthInfo> upperCaseInfos = mentalHealthInfoRepository.findByTitleContainingIgnoreCase("ANXIETY");

        // Verify info was found by title containing (case-insensitive)
        assertThat(managingInfos).hasSize(1);
        assertThat(managingInfos.get(0).getTitle()).isEqualTo("Managing Anxiety");

        assertThat(copingInfos).hasSize(1);
        assertThat(copingInfos.get(0).getTitle()).isEqualTo("Coping with Depression");

        assertThat(reliefInfos).hasSize(1);
        assertThat(reliefInfos.get(0).getTitle()).isEqualTo("Stress Relief Techniques");

        assertThat(techniqueInfos).hasSize(1);
        assertThat(techniqueInfos.get(0).getTitle()).isEqualTo("Stress Relief Techniques");

        // Verify case-insensitivity
        assertThat(lowerCaseInfos).hasSize(1);
        assertThat(lowerCaseInfos.get(0).getTitle()).isEqualTo("Managing Anxiety");

        assertThat(upperCaseInfos).hasSize(1);
        assertThat(upperCaseInfos.get(0).getTitle()).isEqualTo("Managing Anxiety");
    }
}