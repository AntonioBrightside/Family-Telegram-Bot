package com.amam.familybot.repository;

import com.amam.familybot.entity.SleepTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SleepTimeRepository extends JpaRepository<SleepTime, Long> {
    Optional<SleepTime> findByMessageId(long messageId);
}
