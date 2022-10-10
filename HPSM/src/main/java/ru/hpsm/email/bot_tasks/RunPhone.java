package ru.hpsm.email.bot_tasks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hpsm.email.*;

import java.util.Map;
import java.util.Set;

public class RunPhone extends AbstractTask {
    private Set<String> allPhones;

    public RunPhone(Map<String, PartMail> taskList, TelegramBot bot, Set<String> allPhones) {
        super(taskList, bot);
        this.allPhones = allPhones;
    }

    @Override
    public void accept(Message message) {
        if (!allPhones.isEmpty()) {
            try {

                bot.execute(SendMessage.builder().chatId(message.getChatId().toString()).text(allPhones.toString()).build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            try {

                bot.execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(settings.getString(Settings.KeysString.TELEGRAM_RUNPHONE_EMPTY))
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
