package com.amam.familybot.service;

import com.amam.familybot.entity.SleepTime;
import com.amam.familybot.exception.SleepTimeException;
import com.amam.familybot.exception.SleepTimeNotFoundException;
import com.amam.familybot.repository.SleepTimeRepository;
import org.springframework.cglib.core.Local;
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

        if (matcherTime.matches()) {
            LocalTime fallAsleepTime = LocalTime.parse(matcherTime.group(1), DateTimeFormatter.ofPattern("HH:mm"));
            return save(messageId, LocalDate.now(), fallAsleepTime, null, replyMessageId);
        }

        if (replyMessageId != 0L && matcherReply.matches()) {
            LocalTime wakeUpTime = LocalTime.parse(userMessage, DateTimeFormatter.ofPattern("HH:mm"));
            return save(messageId, null, null, wakeUpTime, replyMessageId);
        }

        if (hasDate(userMessage)) {
            Matcher matcher = patternDateTime.matcher(userMessage);
            // TODO: Code
        }

        return "Done";
    }

    // Может можно полиморфизм использовать как-то?
    private Boolean hasDate(String userMessage) {
        return patternDateTime.matcher(userMessage).matches();
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
