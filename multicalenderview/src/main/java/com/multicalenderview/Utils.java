package com.multicalenderview;

import java.util.Calendar;

public class Utils {
    public static boolean isSameDate(Calendar calendar1, Calendar calendar2){
        int day = calendar1.get(Calendar.DAY_OF_MONTH);

        return isSameMonth(calendar1, calendar2) && (day == calendar2.get(Calendar.DAY_OF_MONTH));
    }

    public static boolean isSameWeek(Calendar calendar1, Calendar calendar2){
        int week1 = calendar1.get(Calendar.WEEK_OF_YEAR);
        int week2 = calendar2.get(Calendar.WEEK_OF_YEAR);

        return isSameYear(calendar1, calendar2) && week1 == week2;
    }

    public static boolean isSameMonth(Calendar calendar1, Calendar calendar2){
        int month = calendar1.get(Calendar.MONTH);

        return isSameYear(calendar1,calendar2) && (month == calendar2.get(Calendar.MONTH));
    }

    public static boolean isSameQuarter(Calendar calendar1, Calendar calendar2){
        int quarter1 = calendar1.get(Calendar.MONTH) / 3 + 1;
        int quarter2 = calendar2.get(Calendar.MONTH) / 3 + 1;

        return isSameYear(calendar1,calendar2) && quarter1 == quarter2;
    }

    public static boolean isSameYear(Calendar calendar1, Calendar calendar2){
        int year = calendar1.get(Calendar.YEAR);

        return year == calendar2.get(Calendar.YEAR);
    }

}
