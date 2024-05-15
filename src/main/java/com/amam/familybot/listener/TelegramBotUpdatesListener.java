package com.amam.familybot.listener;

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

                // To check if the user is allowed to use bot
                if (Arrays.stream(allowedUsers).
                        anyMatch(id -> update.message().chat().id() == id)) {
                    telegramBot.execute(new SendMessage(chatId, "Послушал тебя успешно, сын мой"));
                    sendMessage(chatId, sleepTimeService.parseUserMessage(messageText, messageId, replyMessageId));
                } else {
                    sendMessage(chatId, "доступ запрещён");
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
