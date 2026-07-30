package net.jojoaddison.abofonsa.preview.repository;

import net.jojoaddison.abofonsa.preview.domain.LaunchSetting;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LaunchSetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaunchSettingRepository extends JpaRepository<LaunchSetting, Long> {}
