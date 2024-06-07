package com.amam.familybot.command;


import com.amam.familybot.service.SleepTimeService;

import java.time.LocalDate;

public enum Command {

    START("/start") {
        @Override
        public String action() {
            return "Добро пожаловать, хозяин. Приказывай";
        }
    },
    HELP("/help") {
        @Override
        public String action() {
            return """
                    Основная функция бота - записывать, выдавать информацию по командам о количестве сна, оповещать пользователя по временному интервалу.
                    Бот принимает команды в следующем формате:
                    
                        Запись:
                            - время засыпания: 12:00 сон
                            - время засыпания: 12:00 01.01 сон
                            - время пробуждения: 13:00 (необходимо ответить на сообщение, в котором ребёнок заснул)
                        
                        Команды:
                            - /yesterday - получить время сна ребёнка за вчерашний день
                            
                    Удачи!   
                    """;
        }

    },

    YESTERDAY("/yesterday") {
        @Override
        public String action() {
            LocalDate date = LocalDate.now().minusDays(1);
            return sleepTimeService.getSleepTimeByDate(date);
        }
    },

    TODAY("/today") {
        @Override
        public String action() {
            LocalDate date = LocalDate.now();
            return sleepTimeService.getSleepTimeByDate(date);
        }
    };

    private static SleepTimeService sleepTimeService;
    private final String textCommand;


    Command(String textCommand) {
        this.textCommand = textCommand;
    }

    /**
     * To inject SleepTimeService
     * @param service SleepTimeService
     */
    public static void setSleepTimeService(SleepTimeService service) {
        sleepTimeService = service;
    }

    /**
     *
     * @return Name of the command
     */
    public String getTextCommand() {
        return textCommand;
    }

    /**
     * Some action that every command should do for the bot
     * @return String result to send message to user
     */
    abstract public String action();
}
