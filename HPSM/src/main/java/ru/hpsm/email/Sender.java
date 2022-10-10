package ru.hpsm.email;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sender implements Runnable {

    private ConcurrentHashMap<String, PartMail> taskList;
    private TelegramBot telegramBot;
    private final Settings settings;


    Sender(ConcurrentHashMap<String, PartMail> taskList, TelegramBot telegramBot) {
        this.taskList = taskList;
        this.telegramBot = telegramBot;

        settings = Settings.getInstance();
    }

    @Override
    public void run() {
        while (true) {

            if (!taskList.isEmpty()) {
                for (Map.Entry<String, PartMail> entry : taskList.entrySet()) {

                    if (entry.getValue().isNew()) {
                        telegramBot.sendMsg(settings.getListString(Settings.Chats.TELGERAM_CHAT_ALLOW)
                                , entry.getValue().getBody()
                                , Arrays.asList(entry.getValue().getNumberTask())
                                , settings.getString(Settings.KeysString.TELEGRAM_BUTTONS_CALLBACK));
                        entry.getValue().setNew(false);
                        try {
                            Thread.sleep(settings.getInteger(Settings.KeysInteger.MAIL_TO_TELEGRAM_noSpamTimer));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                Thread.sleep(settings.getInteger(Settings.KeysInteger.MAIL_TO_TELEGRAM_updateTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
