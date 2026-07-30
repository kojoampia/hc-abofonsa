package net.jojoaddison.abofonsa.preview.repository;

import java.util.List;
import java.util.Optional;
import net.jojoaddison.abofonsa.preview.domain.LaunchSetting;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LaunchSetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaunchSettingRepository extends JpaRepository<LaunchSetting, Long> {
    Optional<LaunchSetting> findBySettingKeyAndActiveIsTrue(String settingKey);
}
