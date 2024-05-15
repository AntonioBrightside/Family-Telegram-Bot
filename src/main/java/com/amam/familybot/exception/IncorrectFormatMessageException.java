package com.amam.familybot.exception;

public class IncorrectFormatMessageException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Некорректный формат сообщения";
    }
}
