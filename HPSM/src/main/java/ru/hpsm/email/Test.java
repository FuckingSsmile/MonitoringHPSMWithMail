package ru.hpsm.email;

import ru.hpsm.email.time.Day;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Test {
    public static void main(String[] args) {

        String date_s = "Thu Jul 07 15:26:07 MSK 2022";

//        DateTimeFormatter dft2 = DateTimeFormatter
//                .ofPattern("E MMM dd HH:mm:ss z yyyy");

        SimpleDateFormat dt = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");


        try {
            Date date  = dt.parse(date_s);

            SimpleDateFormat dt1 = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
            System.out.println(dt1.format(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

//        LocalDateTime localDateTime = LocalDateTime.parse(date, dft2);
//
//        System.out.println(localDateTime);
//
//        String format = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss")
//                .format(localDateTime);
//
//        System.out.println(format);


    }
}
