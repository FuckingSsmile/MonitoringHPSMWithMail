package ru.hpsm.email;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static ru.hpsm.email.Settings.*;

public class Notifier implements Runnable {

    private ConcurrentHashMap<String, PartMail> taskList;
    private TelegramBot telegramBot;
    private Smsc smsc;
    private LinkedList<SmsPackage> smsPackages;

    private LocalTime starthours = LocalTime.of(8,00);
    private LocalTime endhours = LocalTime.of(22,00);

    private final Settings settings;

    public Notifier(ConcurrentHashMap<String, PartMail> taskList, TelegramBot telegramBot, Set<String> allPhones) {
        this.taskList = taskList;
        this.telegramBot = telegramBot;

        settings = Settings.getInstance();

        smsPackages = new LinkedList<>();
        smsc = new Smsc(settings.getString(KeysString.SMSC_LOGIN)
                ,settings.getString(KeysString.SMSC_PASSWORD)
                ,smsPackages
                ,allPhones);

        new Thread(smsc).start();
    }

    @Override
    public void run() {
        while (true) {
            List<String> awaitingTasks = new ArrayList<>();
            List<String> awaitingTasksForSms = new ArrayList<>();
            List<String> awaitingTasksForCall = new ArrayList<>();

            try {
                Thread.sleep(settings.getInteger(KeysInteger.TIMEOUT_CHECK_AWAITINGTASKS) );

                LocalDateTime now = LocalDateTime.now();

                if (!taskList.isEmpty()) {
                    //Проверка по времени
                    for (Map.Entry<String, PartMail> entry : taskList.entrySet()) {
                        long minutes = ChronoUnit.MINUTES.between(entry.getValue().getLocalDateTime(), now);
                        //Заявка в ожидании перед отправкой в чат (10 минут)
                        if(minutes == settings.getInteger(KeysInteger.DELAY_SEND_MESSAGE_CHAT)){
                            awaitingTasks.add(entry.getValue().getNumberTask());
                            continue;
                        }
                        //Заявка в ожидании перед смс (15мин)
                        if (minutes == settings.getInteger(KeysInteger.DELAY_SEND_SMS)){
                            awaitingTasksForSms.add(entry.getValue().getNumberTask());
                            continue;
                        }
                        //Заявка в ожидании перед звонком (20мин)
                        if (minutes == settings.getInteger(KeysInteger.DELAY_CALL)){
                            DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
                            boolean contains = settings.getDay(AllowCalls.valueOf(dayOfWeek.toString()))
                                    .contains(LocalTime.now());


                            if(LocalTime.now().isAfter(starthours) && LocalTime.now().isBefore(endhours)) {
                                awaitingTasksForCall.add(entry.getValue().getNumberTask());
                            }else {telegramBot.sendMsg(settings.getListString(Chats.TELGERAM_CHAT_ALLOW)
                                    ,settings.getString(KeysString.TEXT_NOT_WORKING_HOURS)
                                    ,awaitingTasksForCall
                                    ,settings.getString(KeysString.TELEGRAM_BUTTONS_CALLBACK));}
                        }

                    }
                    //происходит проверка массива и рассылка
                    if (!awaitingTasks.isEmpty()) {
                        telegramBot.sendMsg(settings.getListString(Chats.TELGERAM_CHAT_ALLOW)
                                , settings.getString(KeysString.TEXT_TICKET_NOT_TAKE_WORK)
                                , awaitingTasks
                                , settings.getString(KeysString.TELEGRAM_BUTTONS_CALLBACK));
                    }

                    if (!awaitingTasksForSms.isEmpty()) {
                        telegramBot.sendMsg(settings.getListString(Chats.TELGERAM_CHAT_ALLOW)
                                , settings.getString(KeysString.TEXT_SEND_SMS_TO_TELEGRAM)
                                , awaitingTasksForSms
                                , settings.getString(KeysString.TELEGRAM_BUTTONS_CALLBACK));
                        smsPackages.add(new SmsPackage(settings.getString(KeysString.TEXT_SEND_SMS_TO_PHONE)
                                + awaitingTasksForSms
                                ,0
                                ,""));

                    }

                    if (!awaitingTasksForCall.isEmpty()) {
                        telegramBot.sendMsg(settings.getListString(Chats.TELGERAM_CHAT_ALLOW)
                                , settings.getString(KeysString.TEXT_CALL_TO_TELEGRAM)
                                , awaitingTasksForCall
                                , settings.getString(KeysString.TELEGRAM_BUTTONS_CALLBACK));
                        smsPackages.add(new SmsPackage(settings.getString(KeysString.TEXT_TO_CALL)
                                +awaitingTasksForCall
                                , 9
                                ,"voice=w2&param=3,0,0"));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
