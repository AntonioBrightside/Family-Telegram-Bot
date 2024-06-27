package com.amam.familybot.repository;

import com.amam.familybot.dto.SleepTimeSummary;
import com.amam.familybot.entity.SleepTime;
import org.springframework.cglib.core.Local;
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

//    @Query(nativeQuery = true,
//            value = "SELECT date, SUM(CAST(sleep_time as interval)) " +
//                    "FROM sleep_time WHERE date BETWEEN :dateFrom AND :dateTill" +
//                    "GROUP BY date ORDER BY date DESC")
//    List<SleepTimeSummary> findSleepTimeByDates(@Param("dateFrom") LocalDate dateFrom,
//                                                @Param("dateTill") LocalDate dateTill);

////    V.3: Работает
//    @Query(nativeQuery = true,
//            value = "SELECT date, SUM(CAST(sleep_time as interval)) " +
//                    "FROM sleep_time WHERE date=:dateFrom GROUP BY date")
//    List<Object[]> findSleepTimeByDates(@Param("dateFrom") LocalDate dateFrom);

//    V.4: Работает
//    @Query(nativeQuery = true,
//            value = "SELECT date, SUM(CAST(sleep_time as interval)) " +
//                    "FROM sleep_time WHERE date BETWEEN :dateFrom AND :dateTill " +
//                    "GROUP BY date " +
//                    "ORDER BY date DESC")
//    List<Object[]> findSleepTimeByDates(@Param("dateFrom") LocalDate dateFrom,
//                                        @Param("dateTill") LocalDate dateTill);

    @Query(nativeQuery = true,
            value = "SELECT date, SUM(CAST(sleep_time as interval)) " +
                    "FROM sleep_time WHERE date BETWEEN :dateFrom AND :dateTill " +
                    "GROUP BY date " +
                    "ORDER BY date DESC")
    List<Object[]> findSleepTimeByDates(@Param("dateFrom") LocalDate dateFrom,
                                                @Param("dateTill") LocalDate dateTill);
}
