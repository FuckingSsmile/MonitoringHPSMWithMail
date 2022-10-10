package ru.hpsm.email;

import ru.hpsm.email.time.Day;
import ru.hpsm.email.time.Interval;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Settings implements Serializable {

    private static Settings instance = null;

    private static String separator = File.separator;

    private static final String DEFAULT_FILE = System.getProperty("user.dir") + separator +"settings";
    private HashMap<Keys, Object> sharedPreferences;


    private Settings(String source) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(source));
            sharedPreferences = (HashMap<Keys, Object>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            sharedPreferences = new HashMap<>();
        }
    }

    private Settings() {
        this(DEFAULT_FILE);
    }


    public synchronized static Settings getInstance() {
        if(instance == null){
            instance = new Settings();
        }
        return instance;
    }


    public String getString(Keys<String> key) {
        return getObject(key, key.getDefaultValue());
    }

    public Boolean getBoolean(Keys<Boolean> key) {
        return getObject(key, key.getDefaultValue());
    }

    public Integer getInteger(Keys<Integer> key) {
        return getObject(key, key.getDefaultValue());
    }

    public Long getLong(Keys<Long> key) {
        return getObject(key, key.getDefaultValue());
    }

    public List<String> getListString(Keys<List<String>> key){
        return getObject(key, key.getDefaultValue());
    }

    public Day getDay(Keys<Day> key) {
        return getObject(key, key.getDefaultValue());
    }

    private <T> T getObject(Keys key, T defaultValue) {
        Object o;
        try {
            o = sharedPreferences.get(key);
        } catch (ClassCastException | NullPointerException e) {
            sharedPreferences.put(key, key.getDefaultValue());
            o = defaultValue;
        }

        if(o == null) o = defaultValue;
        return (T) o;
    }

    public  <T> void setObject(Keys<T> key, T newValue) {
        sharedPreferences.put(key, newValue);
    }

    public boolean save() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(DEFAULT_FILE));

            objectOutputStream.writeObject(sharedPreferences);
            objectOutputStream.flush();
            objectOutputStream.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    //TODO Записать все настройки
    public enum KeysInteger implements Keys<Integer> {
        MAIL_PORT(993),
        MAIL_TO_TELEGRAM_updateTime(1000),
        MAIL_TO_TELEGRAM_noSpamTimer(1000),

        SMSC_ANTISPAM_SEC(3),

        CONTROLLER_TIMEOUT_SAVE_MIN(1),

        DELAY_SEND_MESSAGE_CHAT(10),
        DELAY_SEND_SMS(15),
        DELAY_CALL(20),

        TIMEOUT_CHECK_AWAITINGTASKS(1000 * 60),

        TELEGRAM_COUNT_COLUMNS(3),
        ;

        private int defaultValue;

        KeysInteger(int defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Integer getDefaultValue() {
            return defaultValue;
        }
    }

    //TODO определить группы(например MAIL), заполнить все настройки
    public enum KeysString implements Keys<String> {

        //Проверка ящика для прода
        MAIL_FOLDER_NAME("Support emias"),

        //проверка ящика для теста
        //MAIL_FOLDER_NAME("TEST"),

        MAIL_HOST("imap.yandex.ru"),
        MAIL_KEYPROPERTY("mail.imap.socketFactory.class"),
        MAIL_VALUEPROPERTY("javax.net.ssl.SSLSocketFactory"),
        MAIL_PROTOCOL("imap"),

        MAIL_TITLE_INCIDENT("назначен Инцидент"),
        MAIL_TITLE_REQUEST("Общий запрос"),
        MAIL_TITLE_BACK_TO_WORK("возвращен в работу."),

        MAIL_ADD_LETTER_TO_BUTTON_K("РГ ЕМИАС.Контингент"),
        MAIL_LETTER_BUTTON_K("К-"),
        MAIL_ADD_LETTER_TO_BUTTON_E("РГ ЕМИАС.ЕРП"),
        MAIL_LETTER_BUTTON_E("E-"),
        MAIL_ADD_LETTER_TO_BUTTON_SH("РГ ЕМИАС.ЕРП.Школы"),
        MAIL_LETTER_BUTTON_SH("Ш-"),

        MAIL_DateTimeFormatter("E MMM dd HH:mm:ss z yyyy"),
        MAIL_DateTimeFormatter_PATTERN("yyyy/MM/dd - HH:mm:ss"),

        TELEGRAM_BUTTONS_CALLBACK(" Взял в работу"),
        TELEGRAM_ADD_PHONE("Введите номер, в формате +79991234567"),
        TELEGRAM_REMOVE_PHONE("Для удаления, введите номер, в формате +79991234567"),
        TELEGRAM_ADD_PHONE_SUCCESS(" - Добавлен"),
        TELEGRAM_ADDPHONE_WRONGFORMAT("Неверный формат или такой номер уже существует.\nВведите команду заново"),
        TELEGRAM_DELETEPHONE_SUCCESS(" - Удален"),
        TELEGRAM_RUNPHONE_EMPTY("Список пуст"),
        TELEGRAM_RUNLIST_TASKS("Список задач"),
        TELEGRAM_RUNLIST_EMPTY("Новых задач нет"),

        TEXT_TICKET_NOT_TAKE_WORK("Больше 10 минут в статусе назначен"),
        TEXT_SEND_SMS_TO_TELEGRAM("Больше 15 минут в статусе назначен\nПроизведена рассылка по СМС"),
        TEXT_SEND_SMS_TO_PHONE("Больше 15 минут в статусе назначен "),
        TEXT_CALL_TO_TELEGRAM("Больше 20 минут в статусе назначен\n Произведен обзвон"),
        TEXT_TO_CALL("Есть задачи, более 20 минут в статусе назначен, номер "),
        TEXT_NOT_WORKING_HOURS("Больше 20 минут в статусе назначен\n Нерабочее время для обзвона"),


        ;

        private String defaultValue;

        KeysString(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }
    }


    public enum KeysBoolean implements Keys<Boolean> {
        FIRST_LAUNCH(true);

        private Boolean defaultValue;

        KeysBoolean(Boolean defaultValue) {
            this.defaultValue = false;
        }

        @Override
        public Boolean getDefaultValue() {
            return defaultValue;
        }
    }

    public enum Chats implements Keys<List<String>>{

        private List<String> strings;

        Chats(List<String> strings){
            this.strings = strings;
        }

        @Override
        public List<String> getDefaultValue() {
            return strings;
        }
    }

    //TODO Заполнить интервалы
    public enum AllowCalls implements Keys<Day> {
        MONDAY(
                new Day(Arrays.asList(
                   // new Interval(LocalTime.of(9,0), LocalTime.of(12, 0)),
                    new Interval(8, 0, 18, 0)
                    //new Interval(16, 22)
                )
            )
        ),

        TUESDAY(new Day(Arrays.asList(new Interval(8, 0, 18, 0)))),
        WEDNESDAY(new Day(Arrays.asList(new Interval(8, 0, 18, 0)))),
        THURSDAY(new Day(Arrays.asList(new Interval(8, 0, 18, 0)))),
        FRIDAY(new Day(Arrays.asList(new Interval(8, 0, 18, 0)))),
        SATURDAY(new Day(Arrays.asList(new Interval(8, 0, 18, 0)))),
        SUNDAY(new Day(Arrays.asList(new Interval(8, 0, 18, 0))));

        private Day day;

        AllowCalls(Day day) {
            this.day = day;
        }

        @Override
        public Day getDefaultValue() {
            return day;
        }
    }
}
