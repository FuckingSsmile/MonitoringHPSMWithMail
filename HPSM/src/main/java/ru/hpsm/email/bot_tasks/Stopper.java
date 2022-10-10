package ru.hpsm.email.bot_tasks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hpsm.email.*;

import java.util.List;
import java.util.Map;

public class Stopper extends AbstractTask {
    public Stopper(Map<String, PartMail> taskList, TelegramBot bot) {
        super(taskList, bot);
    }

    @Override
    public void accept(Message message) {
        List<String> whiteList = settings.getListString(Settings.Chats.TELGERAM_USER_ALLOW);
        if(whiteList.contains(message.getFrom().getId().toString())){
            try {

                bot.execute(SendMessage
                        .builder()
                        .chatId(message.getChatId().toString())
                        .text("Работа успешно остановлена\nПользователь " + message.getFrom().getUserName())
                        .parseMode("HTML")
                        .build());



            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }else {
            try {

                bot.execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Работа не остановлена\nПользователь: "
                                + message.getFrom().getId()
                                + " "
                                + message.getFrom().getUserName()
                                + " не входит в группу доверенных: "
                                + whiteList)
                        .parseMode("HTML")
                        .build());


            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
