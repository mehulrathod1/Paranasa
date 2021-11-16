package com.ni.parnasa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyDateUtil {

    private static MyDateUtil instance;

    private MyDateUtil() {

    }

    public static MyDateUtil getInstance() {
        if (instance == null)
            instance = new MyDateUtil();
        return instance;
    }

    public String formateDateTime(String jobDate) throws ParseException {
        // 2019-07-25 00:00:00
        String[] tmp = new String[2];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();

        cal.setTime(dateFormat.parse(jobDate));

        SimpleDateFormat dateFormatResponse = new SimpleDateFormat("MMM dd, yyyy | hh:mm aaa");

        return dateFormatResponse.format(cal.getTime()).toString();

    }

    public String[] formateDateTime2(String jobDate) throws ParseException {
        // 2019-07-25 00:00:00
        String[] tmp = new String[2];

        tmp[0] = jobDate.substring(0, 10);
        tmp[1] = jobDate.substring(11, 16);

        return tmp;
    }
}
