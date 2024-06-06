package com.amam.familybot.listener;

import com.amam.familybot.command.Command;
import com.amam.familybot.exception.IncorrectFormatMessageException;
import com.amam.familybot.exception.SleepTimePeriodException;
import com.amam.familybot.service.SleepTimeService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    long[] allowedUsers = {532690532};

    private final TelegramBot telegramBot;
    private final SleepTimeService sleepTimeService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, SleepTimeService sleepTimeService) {
        this.telegramBot = telegramBot;
        this.sleepTimeService = sleepTimeService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {

                long chatId = update.message().chat().id();
                String messageText = update.message().text();
                long messageId = update.message().messageId();

                long replyMessageId = 0L;
                if (update.message().replyToMessage() != null) {
                    replyMessageId = update.message().replyToMessage().messageId();
                }

                try {
                    // To check if the user is allowed to use bot
                    if (Arrays.stream(allowedUsers).anyMatch(id -> update.message().chat().id() == id)) {

                        // To check command "\yesterday" TODO: Хотелось бы сделать в одну строку метчинг команды и action
                        if (messageText.equals(Command.YESTERDAY.getTextCommand())) {
                            sendMessage(chatId, Command.YESTERDAY.action());
                        } else {
                            sendMessage(chatId, sleepTimeService.parseUserMessage(messageText, messageId, replyMessageId));
                        }
//                        sendMessage(chatId, sleepTimeService.getYesterdaySleepTime());
                    } else {
                        sendMessage(chatId, "доступ запрещён");
                    }
                } catch (IncorrectFormatMessageException e) {
                    sendMessage(chatId, e.getMessage());
                } catch (SleepTimePeriodException e) {
                    sendMessage(chatId, e.getMessage());
                }

            });
        } catch (Exception e) {
            e.getStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendMessage(long chatId, String messageText) {
        SendResponse sendResponse = telegramBot.execute(new SendMessage(chatId, messageText));
        if (!sendResponse.isOk()) {
//            logger.error("Send message is unsuccessful {}", sendResponse.message()); // TODO: Add logger
        }
    }

}
