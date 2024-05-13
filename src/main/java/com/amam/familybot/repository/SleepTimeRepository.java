package com.amam.familybot.repository;

import com.amam.familybot.entity.SleepTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepTimeRepository extends JpaRepository<SleepTime, Long> {
}
