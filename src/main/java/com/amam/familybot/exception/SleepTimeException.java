package com.amam.familybot.exception;

public class SleepTimeException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Формат записи некорректен";

    }
}
