package com.amam.familybot.service;

import com.amam.familybot.entity.SleepTime;
import com.amam.familybot.exception.SleepTimeException;
import com.amam.familybot.repository.SleepTimeRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SleepTimeService {
    private final Pattern patternTime = Pattern.compile("(\\d{1,2}:\\d{2})\\s+(сон)");
    private final Pattern patternDateTime = Pattern.compile("(\\d{2}\\.\\d{2}) (\\d{1,2}:\\d{2}) (сон)");

    private final SleepTimeRepository sleepTimeRepository;

    public SleepTimeService(SleepTimeRepository sleepTimeRepository) {
        this.sleepTimeRepository = sleepTimeRepository;
    }


    public String parseUserMessage(String userMessage, long messageId) {

        if (hasDate(userMessage)) {
            Matcher matcher = patternDateTime.matcher(userMessage);
            // TODO: Code
        }

        if (hasOnlyTime(userMessage)) {
            Matcher matcher = patternTime.matcher(userMessage);
            matcher.find(); // Без него или схожей проверки НЕ РАБОТАЕТ matcher.group()
            LocalDate date = LocalDate.now();
            System.out.println(matcher.group(1)); // DELETE
            LocalTime fallAsleepTime = LocalTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("HH:mm"));

            save(messageId, date, fallAsleepTime, null, null);
            return "Сон успешно сохранён!";
        }


        return "Done";
    }

    // Может можно полиморфизм использовать как-то?
    private Boolean hasDate(String userMessage) {
        return patternDateTime.matcher(userMessage).matches();
    }

    private Boolean hasOnlyTime(String userMessage) {
        if (patternTime.matcher(userMessage).matches()) {
            return true;
        } else {
            throw new SleepTimeException();
        }
    }

    @Transactional
    private void save(long messageId, LocalDate date, LocalTime fallASleepTime,
                      @Nullable LocalTime wakeUpTime, @Nullable LocalTime sleepTime) {

        System.out.println("Trying to save sleep time"); // DELETE

        SleepTime sleep = new SleepTime();
        sleep.setMessageId(messageId);
        sleep.setDate(date);
        sleep.setFallAsleepTime(fallASleepTime);
        sleep.setWakeUpTime(wakeUpTime);
        sleep.setSleepTime(sleepTime);

        sleepTimeRepository.save(sleep);
        System.out.println("save is done"); // DELETE

    }


}
