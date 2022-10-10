package ru.hpsm.email;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hpsm.email.bot_tasks.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TelegramBot extends TelegramLongPollingBot {

    private final Settings settings;

    private ConcurrentHashMap<String, PartMail> taskList;

    private HashMap<String, Consumer<Message>> commandMap = new HashMap<>();

    private TelegramBotState botState = new TelegramBotState();
    private Set<String> allPhones;

    public TelegramBot(ConcurrentHashMap<String, PartMail> taskList, Set<String> allPhones) {

        this.taskList = taskList;
        this.allPhones = allPhones;
        settings = Settings.getInstance();

        commandMap.put("/runlist", new RunList(taskList, this));
        commandMap.put("/runphone", new RunPhone(taskList, this, allPhones));
        commandMap.put("/addphone", new AddPhone(taskList, this, botState));
        commandMap.put("/removephone", new RemovePhone(taskList, this, botState));
        commandMap.put("/stopwork", new Stopper(taskList, this));

    }

    public void putConsumerTask(String command, Consumer<Message> task){
        commandMap.put(command, task);
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (update.hasMessage()) {

            if (message.hasText()) {

                String text = message.getText();

                //Проверка на юзера, принимает сообщение только от инициатора команды
                if(botState.getUserIdForState() == message.getFrom().getId()) {

                    switch (botState.getState()) {

                        case WaitingNewPhone:
                            if (text.matches("\\+7\\d{10}") && !allPhones.contains(text)) {
                                try {
                                    allPhones.add(text);

                                    execute(SendMessage.builder()
                                            .chatId(message.getChatId().toString())
                                            .text(text + settings.getString(Settings.KeysString.TELEGRAM_ADD_PHONE_SUCCESS))
                                            .build());
                                    botState.stateFree();


                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {

                                    execute(SendMessage.builder()
                                            .chatId(message.getChatId().toString())
                                            .text(settings.getString(Settings.KeysString.TELEGRAM_ADDPHONE_WRONGFORMAT))
                                            .build());



                                    botState.stateFree();
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;

                        case WaitingRemovePhone:
                            if (allPhones.contains(text)) {
                                try {
                                    allPhones.remove(text);

                                    execute(SendMessage.builder()
                                            .chatId(message.getChatId().toString())
                                            .text(text + settings.getString(Settings.KeysString.TELEGRAM_DELETEPHONE_SUCCESS))
                                            .build());

                                    botState.stateFree();
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                try {
                                    execute(SendMessage.builder()
                                            .chatId(message.getChatId().toString())
                                            .text(settings.getString(Settings.KeysString.TELEGRAM_ADDPHONE_WRONGFORMAT))
                                            .build());

                                    botState.stateFree();
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                }

                if(settings.getListString(Settings.Chats.TELGERAM_CHAT_ALLOW).contains(message.getChatId().toString())) {

                    String command = message.getText().replace(getBotUsername(), "").trim();
                    Consumer<Message> messageConsumer = commandMap.get(command);
                    if (messageConsumer != null) {
                        messageConsumer.accept(message);
                    }
                }
            }

        } else if (update.hasCallbackQuery()) {
           String user = update.getCallbackQuery().getFrom().getUserName() != null
                   ? update.getCallbackQuery().getFrom().getUserName()
                   : update.getCallbackQuery().getFrom().getFirstName();

            try {
                execute(SendMessage.builder()
                        .text("@"+user + "" +update.getCallbackQuery().getData())
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .build());

                deleteTask(update.getCallbackQuery().getData());

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getBotUsername() {
        return settings.getString(Settings.KeysString.TELEGRAM_BOT_USERNAME);
    }

    @Override
    public String getBotToken() {
        return settings.getString(Settings.KeysString.TELEGRAM_BOT_TOKEN);
    }

    //метод для отправки сообщений в телеграм чат
    public void sendMsg(String chatId, String text, List<String> buttonNames, String callback) {

        List<List<InlineKeyboardButton>> keyboards = createInlineKeyboards(buttonNames, callback);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboards);

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(inlineKeyboardMarkup)
                    .parseMode("HTML")
                    .build());


        } catch (TelegramApiException e) {
        }
    }
    public void sendMsg(List<String> chats, String text, List<String> buttonNames, String callback) {
        chats.forEach(id -> sendMsg(id, text, buttonNames, callback));
    }

    //метод для удалений заявок из tasklist
    public void deleteTask(String text) {
        String[] partTitle = text.split(" ");
        String num = partTitle[partTitle.length - 1];

        taskList.remove(num);

    }

    //метод для создания клавиатуры
    public List<List<InlineKeyboardButton>> createInlineKeyboards(List<String> buttonNamesList, String callback) {

        List<InlineKeyboardButton> buttons = buttonNamesList.stream().map(text -> InlineKeyboardButton
                        .builder()
                        .text(text)
                        .callbackData(callback + " " + text)
                        .build())
                .collect(Collectors.toList());


        //количетсов кнопок в строку.
        double columns = settings.getInteger(Settings.KeysInteger.TELEGRAM_COUNT_COLUMNS);
        double rowsCount = Math.ceil(buttons.size() / columns);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        Iterator<InlineKeyboardButton> iterator = buttons.iterator();

        for (int i = 0; i < rowsCount; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            rows.add(row);
            for (int j = 0; j < columns & iterator.hasNext(); j++) {
                row.add(iterator.next());
            }
        }
        return rows;
    }
}
