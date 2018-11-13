package specificstep.com.onus.Models;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by ubuntu on 8/3/17.
 */

public class DateTime {

    private static SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

    /**
     * This function return current time.
     * Return time example = 12:59:59
     * @return String current time
     */
    public static String getTime()
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        String s_time = (String.valueOf(hour).length() == 1 ? "0"
                + String.valueOf(hour) : String.valueOf(hour))
                + ":"
                + (String.valueOf(min).length() == 1 ? "0"
                + String.valueOf(min) : String.valueOf(min))
                + ":"
                + (String.valueOf(sec).length() == 1 ? "0"
                + String.valueOf(sec) : String.valueOf(sec));
        return s_time;
    }

    /**
     * This function return current time.
     * Return time example = 29:12:2015
     * @return current date
     */
    public static String getDate()
    {
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        String s_date = (String.valueOf(date).length() == 1 ? "0"
                + String.valueOf(date) : String.valueOf(date))
                + "."
                + (String.valueOf(month).length() == 1 ? "0"
                + String.valueOf(month) : String.valueOf(month))
                + "."
                + (String.valueOf(year).length() == 1 ? "0"
                + String.valueOf(year) : String.valueOf(year));
        return s_date;
    }

    /**
     * Get Date from full date
     * @param date String 01/12/2014
     * @return String 01
     */
    public static String getDayFromFullDate(String date)
    {
        return date.substring(0, 2);
    }

    /**
     * Get Month from full date
     * @param date String 01/12/2014
     * @return String 12
     */
    public static String getMonthFromFullDate(String date)
    {
        return date.substring(3, 5);
    }

    /**
     * get current date and time as String
     *
     * @return
     */
    public static String getDateTimeString()
    {
        final GregorianCalendar now = new GregorianCalendar();
        return mDateTimeFormat.format(now.getTime());
    }

    /**
     * Get current date time.
     * @return String current date time
     */
    public static String getCurrentDateTime() {
        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        return s.toString();
    }
}
