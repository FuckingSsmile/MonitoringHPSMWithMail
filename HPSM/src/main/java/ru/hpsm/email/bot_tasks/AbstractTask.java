package ru.hpsm.email.bot_tasks;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hpsm.email.*;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractTask implements Consumer<Message> {

    protected Map<String, PartMail> taskList;
    protected TelegramBot bot;
    protected Settings settings;

    public AbstractTask(Map<String, PartMail> taskList, TelegramBot bot) {
        this.taskList = taskList;
        this.bot = bot;
        settings = Settings.getInstance();
    }
}
