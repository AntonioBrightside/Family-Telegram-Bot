package com.amam.familybot.service;

import com.amam.familybot.entity.SleepTime;
import com.amam.familybot.exception.IncorrectFormatMessageException;
import com.amam.familybot.exception.SleepTimeNotFoundException;
import com.amam.familybot.repository.SleepTimeRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Transactional
    private String save(long messageId, @Nullable LocalDate date, @Nullable LocalTime fallASleepTime,
                        @Nullable LocalTime wakeUpTime, long replyMessageId) {

        SleepTime sleep = new SleepTime();

        if (replyMessageId != 0L) {
            sleep = sleepTimeRepository.findByMessageId(replyMessageId)
                    .orElseThrow(SleepTimeNotFoundException::new);

            long sleepTimeMinutes = MINUTES.between(sleep.getFallAsleepTime(), wakeUpTime);
            LocalTime sleepTimeHm = LocalTime.MIN.plus(Duration.ofMinutes(sleepTimeMinutes));

            sleep.setWakeUpTime(wakeUpTime);
            sleep.setSleepTime(sleepTimeHm);

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

    private Optional<SleepTime> findMessageId(long messageId) {
        return sleepTimeRepository.findByMessageId(messageId);
    }


}
