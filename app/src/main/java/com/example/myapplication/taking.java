package com.example.myapplication;

import java.util.Date;

class taking<T> {
    final Date timestamp;
    final T reading;

    taking(Date timestamp, T reading) {
        this.timestamp = timestamp;
        this.reading = reading;
    }
}
