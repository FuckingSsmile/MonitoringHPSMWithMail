package ru.hpsm.email.bot_tasks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hpsm.email.*;
import java.util.Map;

public class AddPhone extends AbstractTask{
    private TelegramBotState botState;

    public AddPhone(Map<String, PartMail> taskList, TelegramBot bot,TelegramBotState botState) {
        super(taskList, bot);
        this.botState = botState;
    }

    @Override
    public void accept(Message message) {
        botState.setUserIdForState(message.getFrom().getId());
        String command = message.getText().replace(bot.getBotUsername(), "").trim();
        botState.changeActivityState(command);
            try {
                bot.execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(settings.getString(Settings.KeysString.TELEGRAM_ADD_PHONE))
                        .build());

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
    }
}
