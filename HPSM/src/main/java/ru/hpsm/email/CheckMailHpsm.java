package ru.hpsm.email;

import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class CheckMailHpsm {

    //TODO добавить все поля
    private final Settings settings;

//    private static final String ENCODING = StandardCharsets.UTF_8.name();

    private ConcurrentHashMap<String, PartMail> taskList;
    private Store store;
    private Session session;
    private Folder folder;

    public CheckMailHpsm(ConcurrentHashMap<String, PartMail> taskList) {
//        System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        settings = Settings.getInstance();
        this.taskList = taskList;

        //Объект properties содержит параметры соединения
        Properties properties = new Properties();
        //Так как для чтения Yandex требует SSL-соединения - нужно использовать фабрику SSL-сокетов
//        properties.setProperty(settings.getString(Settings.KeysString.MAIL_KEYPROPERTY),
//                settings.getString(Settings.KeysString.MAIL_VALUEPROPERTY));
        properties.put("mail.smtp.ssl.enable", "true");
        //Создаем соединение для чтения почтовых сообщений
        session = Session.getDefaultInstance(properties);

        //Устанавливаем соединение с почтовым ящиком
        if (store == null) {
            try {
                //Это хранилище почтовых сообщений.
//                store = session.getStore(settings.getString(Settings.KeysString.MAIL_PROTOCOL));
                store = session.getStore("imaps");
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                System.out.println("проблемы при store = session.getStore " + LocalDateTime.now());
                return;
            }
        }

        //Подключаемся к почтовому ящику
        try {
            store.connect(settings.getString(Settings.KeysString.MAIL_HOST),
                    settings.getInteger(Settings.KeysInteger.MAIL_PORT),
                    settings.getString(Settings.KeysString.MAIL_LOGIN),
                    settings.getString(Settings.KeysString.MAIL_PASSWORD));
            folder = store.getFolder(settings.getString(Settings.KeysString.MAIL_FOLDER_NAME));

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("проблемы при store.connect-1 " + LocalDateTime.now());

        }

    }

    public void check() {
        //Устанавливаем соединение с почтовым ящиком
        try {
            if(!store.isConnected()){
                try {
                    store.connect(settings.getString(Settings.KeysString.MAIL_HOST),
                            settings.getInteger(Settings.KeysInteger.MAIL_PORT),
                            settings.getString(Settings.KeysString.MAIL_LOGIN),
                            settings.getString(Settings.KeysString.MAIL_PASSWORD));
                    folder = store.getFolder(settings.getString(Settings.KeysString.MAIL_FOLDER_NAME));

                } catch (MessagingException e) {
                    e.printStackTrace();
                    System.out.println("проблемы при store.connect-2 " + LocalDateTime.now());
                }
            }

            folder.open(Folder.READ_WRITE);


            getText(folder, settings.getString(Settings.KeysString.MAIL_FOLDER_NAME));

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println( "проблемы при folder.open " + LocalDateTime.now());
        }
    }


    public void getText(Folder folder, String name) throws MessagingException {
        try {
            Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            //Циклом пробегаемся по всем сообщениям
            for (Message message : messages) {

                String content = MimeUtility.decodeText((String) message.getContent());
                String title;
                try {
                    title = MimeUtility.decodeWord(message.getSubject());
                } catch (ParseException | UnsupportedEncodingException e) {
                    title = message.getSubject();
                }

                //Парсим дату
//                    String date = ParseDate(message.getReceivedDate());
                String date;
                try {
                    date = MimeUtility.decodeWord(ParseDate(message.getReceivedDate()));
                } catch (ParseException e) {
                    date = ParseDate(message.getReceivedDate());
                }


                //Получить адресата
                //String from = ((InternetAddress) message.getFrom()[0]).getAddress();

                //вытаскиваем номер заявки
                String numberTask = getTitle(title);

                //Проверяем определенную папку
                if (name.equalsIgnoreCase(settings.getString(Settings.KeysString.MAIL_FOLDER_NAME))) {
                    String body = (Jsoup.parse((String) content).text());
                    String[] parts = body.split(" ");
                    StringBuilder builder = new StringBuilder();
                    //Разделяем на разновидность сообщений


                    //1 вид
                    if (title.contains(settings.getString(Settings.KeysString.MAIL_TITLE_INCIDENT))) {

                        PartMail partMail = new PartMail();

                        for (int i = 0; i < parts.length; i++) {
                            if (parts[i].matches("[а-яА-Я0-9:()]{3,100}+(\\.)") || parts[i].matches("Описание:")) {
                                builder.append(parts[i] + " ");
                                builder.append("\n");
                            } else {
                                builder.append(parts[i] + " ");
                            }
                        }

                        String bodyMail = "Дата " + date +
                                "\n<b>" + title + "</b>";

                        partMail.setBody(bodyMail);
                        partMail.setTitle(title);
                        partMail.setLocalDateTime(LocalDateTime.now());
                        partMail.setNumberTask(numberTask);

                        taskList.put(numberTask, partMail);
                    }

                    //2 вид
                    if (title.contains(settings.getString(Settings.KeysString.MAIL_TITLE_REQUEST))) {
                        PartMail partMail = new PartMail();

                        for (int i = 0; i < parts.length; i++) {

                            if (parts[i].matches("\\.") || parts[i].matches("Описание:")) {
                                builder.append("\n");
                            }
                            builder.append(parts[i] + " ");
                        }

                        String bodyMail = "Дата " + date +
                                "\n <b>" + title + "</b>";

                        partMail.setBody(bodyMail);
                        partMail.setTitle(title);
                        partMail.setLocalDateTime(LocalDateTime.now());
                        partMail.setNumberTask(numberTask);

                        taskList.put(numberTask, partMail);
                    }

                    //3 вид
                    if (title.contains(settings.getString(Settings.KeysString.MAIL_TITLE_BACK_TO_WORK))) {
                        PartMail partMail = new PartMail();

                        for (int i = 0; i < parts.length; i++) {

                            if (parts[i].matches("[а-яА-Я0-9:()\"]{3,100}+(\\.)") || parts[i].matches("Описание:")) {
                                builder.append(parts[i] + " ");
                                builder.append("\n");
                            } else {
                                builder.append(parts[i] + " ");
                            }
                        }

                        String bodyMail = "Дата " + date +
                                "\n <b>" + title + "</b>";

                        partMail.setBody(bodyMail);
                        partMail.setTitle(title);
                        partMail.setLocalDateTime(LocalDateTime.now());
                        partMail.setNumberTask(numberTask);

                        taskList.put(numberTask, partMail);
                    }
                }
                //помечаем сообщение как прочитанное
                message.setFlag(Flags.Flag.SEEN, true);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("UnsupportedEncodingException " + LocalDateTime.now());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException " + LocalDateTime.now());
        } finally {
            if (folder != null) {
                folder.close(false);
            }
        }
    }

    public String getTitle(String title) {
        String[] partTitle = title.split(" ");
        // Извлекаем номер задачи IM и RF
        for (int i = 0; i < partTitle.length; i++) {
            if (partTitle[i].matches("\\w{2}\\d+")) {
                if (title.contains(settings.getString(Settings.KeysString.MAIL_ADD_LETTER_TO_BUTTON_SH))) {
                    return settings.getString(Settings.KeysString.MAIL_LETTER_BUTTON_SH) + partTitle[i];
                }
                if (title.contains(settings.getString(Settings.KeysString.MAIL_ADD_LETTER_TO_BUTTON_E))) {
                    return settings.getString(Settings.KeysString.MAIL_LETTER_BUTTON_E) + partTitle[i];
                }
                if (title.contains(settings.getString(Settings.KeysString.MAIL_ADD_LETTER_TO_BUTTON_K))) {
                    return settings.getString(Settings.KeysString.MAIL_LETTER_BUTTON_K) + partTitle[i];
                }
            }
        }
        return "";
    }

    public String ParseDate(Date date) {
//Wed Feb 24 08:14:07 MSK 2021
        //MimeUtility.decodeWord(message.getSubject());

        //СТАРАЯ ВАРИАЦИЯ ПАРСИНГА ДАТЫ
//        DateTimeFormatter dft2 = DateTimeFormatter
//                .ofPattern(settings.getString(Settings.KeysString.MAIL_DateTimeFormatter));
//
//        LocalDateTime localDateTime = LocalDateTime.parse(date.toString(), dft2);
//
//        return DateTimeFormatter.ofPattern(settings.getString(Settings.KeysString.MAIL_DateTimeFormatter_PATTERN))
//                .format(localDateTime);

        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);


        try {
            Date date2  = dt.parse(date.toString());

            SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss", Locale.ENGLISH);

            return dt1.format(date2);


        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }


    }
}

