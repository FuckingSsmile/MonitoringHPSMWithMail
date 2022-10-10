package ru.hpsm.email.time;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Interval implements Serializable {

    private LocalTime start;
    private LocalTime end;

    public Interval(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;

        if(start.isAfter(end)){
            this.start = end;
            this.end = start;
        }
    }

    public Interval(int h1, int m1, int h2, int m2){
        this(LocalTime.of(h1, m1), LocalTime.of(h2, m2));
    }

    public Interval(int h1, int h2){
        this(h1, 0, h2, 0);
    }


    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public boolean contain(LocalTime time){
        return time.isAfter(start) && time.isBefore(end);
    }

    @Override
    public String toString() {
        return "Interval{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
