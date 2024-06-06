package com.amam.familybot.exception;

public class SleepTimePeriodException extends RuntimeException{

    @Override
    public String getMessage() {
        return "За указанный период нет данных в базе";
    }
}
