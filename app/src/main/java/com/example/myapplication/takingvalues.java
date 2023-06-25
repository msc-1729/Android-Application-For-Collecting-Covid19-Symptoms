package com.example.myapplication;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class takingvalues {
    private final CopyOnWriteArrayList<taking<Integer>> takings = new CopyOnWriteArrayList<>();
    private int minimum = 2147483647;
    private int maximum = -2147483648;

    void add(int reading) {
        taking<Integer> takingWithDate = new taking<>(new Date(), reading);

        takings.add(takingWithDate);
        if (reading < minimum) minimum = reading;
        if (reading > maximum) maximum = reading;
    }

    @SuppressWarnings("SameParameterValue")
    CopyOnWriteArrayList<taking<Integer>> getLastStdValues(int count) {
        if (count < takings.size()) {
            return  new CopyOnWriteArrayList<>(takings.subList(takings.size() - 1 - count, takings.size() - 1));
        } else {
            return takings;
        }
    }

    Date getLastTimestamp() {
        return takings.get(takings.size() - 1).timestamp;
    }
}
