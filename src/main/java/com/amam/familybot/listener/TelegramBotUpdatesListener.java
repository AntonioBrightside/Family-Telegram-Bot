package com.amam.familybot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener{

    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
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
                telegramBot.execute(new SendMessage(chatId, "Hello!"));
                System.out.println("For test commit");
            });
        } catch (Exception e) {
            e.getStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
