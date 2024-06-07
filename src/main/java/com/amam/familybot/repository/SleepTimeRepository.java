package com.amam.familybot.repository;

import com.amam.familybot.entity.SleepTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SleepTimeRepository extends JpaRepository<SleepTime, Long> {
    Optional<SleepTime> findByMessageId(long messageId);

    @Query(nativeQuery = true, value = "SELECT * FROM sleep_time WHERE date = :date")
    List<SleepTime> findAllByDate(@Param("date") LocalDate date);

    @Query(nativeQuery = true, value = "SELECT SUM(CAST(sleep_time as interval)) FROM sleep_time WHERE date = :date")
    Optional<String> findSleepTimeByDate(@Param("date") LocalDate date);
}
