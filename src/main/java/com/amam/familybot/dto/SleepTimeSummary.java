package com.amam.familybot.dto;

import org.postgresql.util.PGInterval;

import java.time.LocalDate;

public record SleepTimeSummary(LocalDate date, PGInterval sleepTime) {

    @Override
    public String toString() {
        return String.format("%s: %s:%s", date, sleepTime.getHours(), sleepTime.getMinutes());
    }

    @Override
    public LocalDate date() {
        return date;
    }
}
