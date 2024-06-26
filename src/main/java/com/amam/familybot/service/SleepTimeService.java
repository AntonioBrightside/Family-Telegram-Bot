package com.amam.familybot.service;

import com.amam.familybot.dto.SleepTimeSummary;
import com.amam.familybot.entity.SleepTime;
import com.amam.familybot.exception.IncorrectFormatMessageException;
import com.amam.familybot.exception.SleepTimeNotFoundException;
import com.amam.familybot.exception.SleepTimePeriodException;
import com.amam.familybot.repository.SleepTimeRepository;
import org.hibernate.bytecode.internal.bytebuddy.BytecodeProviderImpl;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.postgresql.util.PGInterval;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.sql.Date;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class SleepTimeService {
    private final Pattern patternTime = Pattern.compile("(\\d{1,2}:\\d{2})\\s+(сон)");
    private final Pattern patternDateTime = Pattern.compile("(\\d{2}\\.\\d{2}) (\\d{1,2}:\\d{2}) (сон)");
    private final Pattern patternReply = Pattern.compile("(\\d{1,2}:\\d{2})");

    private final SleepTimeRepository sleepTimeRepository;

    public SleepTimeService(SleepTimeRepository sleepTimeRepository) {
        this.sleepTimeRepository = sleepTimeRepository;
    }

    /**
     * Parse user's message due to regex and record it to DB
     *
     * @param userMessage    user test message
     * @param messageId      ID message
     * @param replyMessageId the ID of replied message
     * @return bot status
     */
    public String parseUserMessage(String userMessage, long messageId, long replyMessageId) {

        Matcher matcherTime = patternTime.matcher(userMessage);
        Matcher matcherDateTime = patternDateTime.matcher(userMessage);
        Matcher matcherReply = patternReply.matcher(userMessage);

        // To save wakeUpTime
        if (replyMessageId != 0L && matcherReply.matches()) {
            LocalTime wakeUpTime = LocalTime.parse(userMessage, DateTimeFormatter.ofPattern("HH:mm"));
            return save(messageId, null, null, wakeUpTime, replyMessageId);
        }

        // To init new record in DB (user's message w/o date) and save fallAsleepTime
        if (matcherTime.matches()) {
            LocalTime fallAsleepTime = LocalTime.parse(matcherTime.group(1), DateTimeFormatter.ofPattern("HH:mm"));
            return save(messageId, LocalDate.now(), fallAsleepTime, null, replyMessageId);
        }

        // To init new record in DB (user's message with date) and save fallAsleepTime
        if (matcherDateTime.matches()) {
            LocalDate date = LocalDate.parse("%s.%s".formatted(matcherDateTime.group(1),
                    LocalDate.now().getYear()), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            LocalTime fallAsleepTime = LocalTime.parse(matcherDateTime.group(2), DateTimeFormatter.ofPattern("HH:mm"));
            return save(messageId, date, fallAsleepTime, null, replyMessageId);
        }

        throw new IncorrectFormatMessageException();
    }

    /**
     * Save the record to DB. Save in two stages: fall asleep time and wake-up time.
     *
     * @param messageId      message ID
     * @param date           date of sleep
     * @param fallASleepTime fall asleep time
     * @param wakeUpTime     wake-up time
     * @param replyMessageId the ID of replied message
     * @return
     */
    @Transactional
    private String save(long messageId, @Nullable LocalDate date, @Nullable LocalTime fallASleepTime,
                        @Nullable LocalTime wakeUpTime, long replyMessageId) {

        SleepTime sleep = new SleepTime();

        if (replyMessageId != 0L) {
            sleep = sleepTimeRepository.findByMessageId(replyMessageId)
                    .orElseThrow(SleepTimeNotFoundException::new);

            sleep.setWakeUpTime(wakeUpTime);
            sleep.setSleepTime(getSleepTimeInHmFormat(sleep.getFallAsleepTime(), wakeUpTime));

            sleepTimeRepository.save(sleep);
            return "В запись добавлено время пробуждения";
        } else {
            sleep.setMessageId(messageId);
            sleep.setDate(date);
            sleep.setFallAsleepTime(fallASleepTime);
            sleep.setWakeUpTime(wakeUpTime);
            sleep.setSleepTime(null);

            sleepTimeRepository.save(sleep);
            return "Сон успешно сохранён";
        }
    }

    /**
     * Find message by message ID
     *
     * @param messageId
     * @return record from DB or empty Optional
     */
    private Optional<SleepTime> findMessageId(long messageId) {
        return sleepTimeRepository.findByMessageId(messageId);
    }

    /**
     * Count sleep time and return it in necessary format
     *
     * @param fallAsleepTime fall asleep time in 'HH:mm' format
     * @param wakeUpTime     wake-up time in 'HH:mm' format
     * @return sleep time in 'HH:mm' format
     */
    private LocalTime getSleepTimeInHmFormat(LocalTime fallAsleepTime, LocalTime wakeUpTime) {
        long sleepTimeMinutes = MINUTES.between(fallAsleepTime, wakeUpTime);
        return LocalTime.MIN.plus(Duration.ofMinutes(sleepTimeMinutes));
    }

    /**
     * Get Sleep time by Date
     *
     * @param date yesterday or today
     * @return sum of sleep time by date
     */
    public String getSleepTimeByDate(LocalDate date) {
        Optional<String> sleepTime = sleepTimeRepository.findSleepTimeByDate(date);
        return sleepTime.map(value -> {
            String[] splitSleepTime = sleepTime.get().split(" ");
            return String.format("%s:%s - Итоговое время сна за %s", splitSleepTime[6], splitSleepTime[8], date);
        }).orElseThrow(SleepTimePeriodException::new);
    }

    public StringBuilder getSleepTimeBetweenDates(LocalDate dateFrom) {
        System.out.println("Inside BetweenDates method");
//        V.4. WORKS
//        System.out.println(dateFrom);
//        System.out.println(LocalDate.now());
        List<Object[]> result = sleepTimeRepository.findSleepTimeByDates(dateFrom, LocalDate.now());
//        System.out.println("Received data from DB");
//        System.out.println(result);

//        List<SleepTimeSummary> sleepTimeByDates = new ArrayList<>();
//        for (Object[] i : result) {
//            Date date = (Date) i[0];
//            PGInterval time = (PGInterval) i[1];
//            SleepTimeSummary timeSummary = new SleepTimeSummary(date.toLocalDate(), time);
//            sleepTimeByDates.add(timeSummary);
//        }
//
//        for (SleepTimeSummary sleepTimeSummary : sleepTimeByDates) {
//            System.out.println(sleepTimeSummary);
//        }

        List<SleepTimeSummary> sleepTimeByDate = result.stream()
                .map(i -> {
                    Date date = (Date) i[0];
                    PGInterval time = (PGInterval) i[1];
                    return new SleepTimeSummary(date.toLocalDate(), time);
                })
                .toList();

//        for (SleepTimeSummary sleepTimeSummary : A) {
//            System.out.println(sleepTimeSummary);
//        }

        StringBuilder answer = new StringBuilder();

        for (SleepTimeSummary sleepTimeSummary : sleepTimeByDate) {
            answer.append(String.format(sleepTimeSummary.toString() + "\n"));
        }

        return answer;

    }


}

