package ru.hpsm.email.time;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day implements Serializable {
    private Set<Interval> intervals;

    public Day(List<Interval> intervals) {
        this.intervals = new HashSet<>();

        if(intervals != null) this.intervals.addAll(intervals);
    }

    public Day() {
        this(null);
    }


    public boolean contains(LocalTime time){
        for (Interval interval : intervals) {
            if(interval.contain(time)) return true;
        }
        return false;
    }
}
