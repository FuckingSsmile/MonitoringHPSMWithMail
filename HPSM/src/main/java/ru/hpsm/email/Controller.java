package ru.hpsm.email;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public class Controller {

    public static void main(String[] args) {
        int timeoutSaveMin = 1;
        boolean running = true;

        String separator = File.separator;

        String pathPhones = System.getProperty("user.dir") + separator +"phones";
        System.out.println(pathPhones);

        ConcurrentHashMap<String, PartMail> taskList = new ConcurrentHashMap<>();

        Set<String> allPhones;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(pathPhones));
            allPhones = (Set<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            allPhones = new CopyOnWriteArraySet<>();
        }

        final CheckMailHpsm checkMailHpsm = new CheckMailHpsm(taskList);

        Runnable runnable = () -> {
            while (running) {
                checkMailHpsm.check();
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        TelegramBot telegramBot = new TelegramBot(taskList, allPhones);
        telegramBot.putConsumerTask("/hello!", message -> System.out.println("Hello!"));

        try {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }



        Thread senderThread = new Thread(new Sender(taskList, telegramBot));
        senderThread.start();

        Thread notifierThread = new Thread(new Notifier(taskList, telegramBot, allPhones));
        notifierThread.start();


        Thread savePhones = new Thread();
        savePhones.start();
        while(running){
            try {
                Set<String> finalAllPhones = allPhones;
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(pathPhones));

                objectOutputStream.writeObject(finalAllPhones);
                objectOutputStream.flush();
                objectOutputStream.close();

                Thread.sleep(timeoutSaveMin * 1000 * 60);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
