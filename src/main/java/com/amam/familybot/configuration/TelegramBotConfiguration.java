package com.amam.familybot.configuration;

import com.amam.familybot.command.Command;
import com.amam.familybot.service.SleepTimeService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfiguration {

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

    @Autowired
    private SleepTimeService sleepTimeService;

    @Value("${FAMILY_BOT_TOKEN}")
    private String token;

    @PostConstruct
    public void init() {
        Command.setSleepTimeService(sleepTimeService);
    }

}
