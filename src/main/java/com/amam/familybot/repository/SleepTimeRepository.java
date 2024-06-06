package com.amam.familybot.repository;

import com.amam.familybot.entity.SleepTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SleepTimeRepository extends JpaRepository<SleepTime, Long> {
    Optional<SleepTime> findByMessageId(long messageId);

    @Query(nativeQuery = true,
            value = "SELECT SUM(sleep_time) as total FROM sleep_time WHERE date='2024-06-03'") //:date
    LocalTime findSleepTimeByDate(@Param("date") LocalDate date);

    @Query(nativeQuery = true, value = "SELECT * FROM sleep_time WHERE date = :date")
    List<SleepTime> findAllByDate(@Param("date") LocalDate date);

    @Query(nativeQuery = true, value = "SELECT SUM(CAST(sleep_time as interval)) FROM sleep_time WHERE date = :date")
    Optional<String> findSleepTimeByDateTest(@Param("date") LocalDate date);
}
