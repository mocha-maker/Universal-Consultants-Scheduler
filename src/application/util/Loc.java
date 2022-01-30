package application.util;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Localization Helper Class
 * Manages localization resource bundles as well as date-time conversion and formatting
 */
public abstract class Loc {

    /*  ======================
        CLASS VARIABLES
        ======================*/

    protected static ResourceBundle rb;
    protected static Locale activeLocale;
    static TimeZone timezone;


    /*  ======================
        LOCALE & RESOURCE BUNDLES
        ======================*/

    /**
     * Captures and sets the system's current locale and sets the respective resource bundle for the application using protected methods and variables to prevent tampering
     */
    public static void setLocaleBundle() {
        activeLocale = activateLocale();
        rb = ResourceBundle.getBundle("/application/resources/lang", getLocale());
    }

    /**
     *
     * @return the locale language
     */
    protected static Locale activateLocale() {
        Locale locale = Locale.getDefault();
        switch(locale.getLanguage()) {
            case "fr":
                locale = new Locale("fr","CA");
                break;
            case "en":
                break;
            default:
                locale = new Locale("en","US");
        }
        return locale;
    }

    /**
     *
     * @return the previously set resource bundle to use
     */
    public static ResourceBundle getBundle() {
        return rb;
    }

    /**
     *
     * @return the previously set locale
     */
    protected static Locale getLocale() {
        return activeLocale;
    }




    /*  ======================
        TIME CONVERSION & FORMATTING
        ======================*/

    // GETTERS

    /**
     *
     * @return the system's default TimeZone
     */
    public static TimeZone getZone() {
        return TimeZone.getDefault();
    }

    /**
     *
     * @return the current Timestamp in UTC
     */
    public static Timestamp getCurrentTimestamp() {
        return (toTimestamp(convertTo(LocalDateTime.now(),"UTC")));
    }



    // CONVERSIONS

    /**
     * Converts a given Timestamp into a LocalDateTime in the Systems Default Zone
     * @param timestamp - the Timestamp to be converted
     * @return converted LocalDateTime
     */
    public static LocalDateTime timeStampToLocal(Timestamp timestamp) {
        return timestamp.toLocalDateTime().atZone(ZoneId.of("UTC")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Converts a LocalDateTime into a Timestamp
     * @param localDateTime - LocalDateTime to be converted
     * @return converted Timestamp
     */
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    /**
     * General Conversion of a LocalDateTime to LocalDateTime of a given zone.
     * @param localDateTime - time to be converted
     * @return converted LocalDateTime
     */
    public static LocalDateTime convertTo(LocalDateTime localDateTime, String zone) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.of(zone)).toLocalDateTime();
    }

    /**
     * General Conversion of a LocalDateTime to ZonedDateTime of a given zone.
     * @param localDateTime - time to be converted
     * @return converted ZonedDateTime
     */
    public static ZonedDateTime convertToZDT(LocalDateTime localDateTime, String zone) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.of(zone));
    }


    // FORMATTERS

    /**
     * Formats a LocalDateTime into a String of the given pattern.
     * @param localDateTime - the LocalDateTime to be formatted
     * @param pattern - the pattern to use for formatting
     * @return
     */
    public static String dateToString(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Formats a ZonedDateTime into a String of the given pattern.
     * @param zonedDateTime - the ZonedDateTime to be formatted
     * @param pattern - the pattern to use for formatting
     * @return
     */
    public static String dateToString(ZonedDateTime zonedDateTime, String pattern) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    // PARSERS

    /**
     * Parses a string into LocalDateTime
     * @param string
     * @return
     */
    public static LocalDateTime stringToLocal(String string) {
        return LocalDateTime.parse(string);
    }

    /**
     * Formats a LocalDateTime to string that represents the hour
     * @param dt - the date-time to be formatted
     * @return - the formatted hour string
     */
    public static String getHour(LocalDateTime dt) {
        return dateToString(dt,"hh");
    }

    /**
     * Formats a LocalDateTime to string that represents the minute
     * @param dt - the date-time to be formatted
     * @return - the formatted minute string
     */
    public static String getMinute(LocalDateTime dt) {
        return dateToString(dt,"mm");
    }

    /**
     * Formats a LocalDateTime to string that represents the meridiem
     * @param dt - the date-time to be formatted
     * @return - the formatted meridiem string
     */
    public static String getMeridiem(LocalDateTime dt) {
        return dateToString(dt,"a").toUpperCase();
    }

    //


}
