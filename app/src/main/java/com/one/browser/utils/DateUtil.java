package com.one.browser.utils;

import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * @author 18517
 */
public class DateUtil {
    public static String getTime(String time) {
        String outputDateFormat = "yyyy年MM月dd日";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);
        try {
            Date date = inputFormat.parse(time);
            String outputDate = outputFormat.format(date);
            System.out.println(outputDate);
            return outputDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
