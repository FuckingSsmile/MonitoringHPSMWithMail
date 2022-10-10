package ru.hpsm.email.bot_tasks;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hpsm.email.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RunList extends AbstractTask {
    public RunList(Map<String, PartMail> taskList, TelegramBot bot) {
        super(taskList, bot);
    }

    @Override
    public void accept(Message message) {
        List<String> keys = taskList.entrySet().stream()
                .filter(entry -> !entry.getValue().isCompleted())
                .map(e -> e.getKey()).collect(Collectors.toList());
        if (keys.isEmpty()) {
            bot.sendMsg(message.getChatId().toString()
                    , settings.getString(Settings.KeysString.TELEGRAM_RUNLIST_EMPTY)
                    , keys
                    , settings.getString(Settings.KeysString.TELEGRAM_BUTTONS_CALLBACK));
        } else {
            bot.sendMsg(message.getChatId().toString()
                    , settings.getString(Settings.KeysString.TELEGRAM_RUNLIST_TASKS)
                    , keys
                    , settings.getString(Settings.KeysString.TELEGRAM_BUTTONS_CALLBACK));



        }
    }
}
