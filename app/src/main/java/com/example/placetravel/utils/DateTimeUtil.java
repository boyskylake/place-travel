package com.example.placetravel.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

    @SuppressLint("SimpleDateFormat")
    public static String getTimeUTC() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(new Date());
    }

}
