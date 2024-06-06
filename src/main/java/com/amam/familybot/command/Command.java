package com.amam.familybot.command;


import com.amam.familybot.service.SleepTimeService;

public enum Command {

    START("/start") {
        @Override
        public String action() {
            return "Добро пожаловать, хозяин. Приказывай";
        }
    },
//    HELP("/help") {
//
//    },

    YESTERDAY("/yesterday") {
        @Override
        public String action() {
            return sleepTimeService.getYesterdaySleepTime();
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
