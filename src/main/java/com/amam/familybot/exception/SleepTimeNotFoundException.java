package com.amam.familybot.exception;

public class SleepTimeNotFoundException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Нет такой записи в базе";
    }
}
