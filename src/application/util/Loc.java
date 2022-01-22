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
    static ResourceBundle rb;
    static Locale activeLocale;
    static TimeZone timezone;


    /*  ======================
        LOCALE & RESOURCE BUNDLES
        ======================*/

    public static void setLocaleBundle() {
        activeLocale = activateLocale();
        rb = ResourceBundle.getBundle("/application/resources/lang", activeLocale);
    }

    public static Locale activateLocale() {
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

    public static Locale getLocale() {
        return activeLocale;
    }

    public static ResourceBundle getBundle() {
        return rb;
    }


    /*  ======================
        TIME CONVERSION & FORMATTING
        ======================*/

    // GETTERS

    public static Timestamp getCurrentTimestamp() {
        return (toTimestamp(toUTC(LocalDateTime.now())));
    }

    public static LocalDate getCurrentLocalDate() { return (LocalDateTime.now().toLocalDate()); }

    public static LocalTime getCurrentLocalTime() { return (LocalDateTime.now().toLocalTime()); }

    // For use in Calendar Controller

    public static LocalDate getFirstOfMonth(LocalDate localDate) {
        return localDate.withDayOfMonth(1);
    }

    public static LocalDate getBeginOfWeek(LocalDate localDate) {
        DayOfWeek firstDayofWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        return localDate.with(TemporalAdjusters.previousOrSame(firstDayofWeek));
    }

    public static LocalDate getNextMonth(LocalDate localDate) {
        return getFirstOfMonth(localDate.plusMonths(1));
    }

    public static LocalDate getNextWeek(LocalDate localDate) {
        return getBeginOfWeek(localDate.plusWeeks(1));
    }

    public static LocalDate getPrevMonth(LocalDate localDate) {
        return getFirstOfMonth(localDate.minusMonths(1));
    }

    public static LocalDate getPrevWeek(LocalDate localDate) {
        return getBeginOfWeek(localDate.minusWeeks(1));
    }


    // CONVERSIONS

    // TODO: Convert java.sql.Date from database to LocalDateTime
    public static LocalDateTime timeStampToLocal(Timestamp timestamp) {
        return timestamp.toLocalDateTime().atZone(ZoneId.of("UTC")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // TODO: Convert LocalDateTime to Timestamp for recording into database
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    public static Timestamp toTimestamp(LocalDate localDate) {
        return Timestamp.valueOf(localDate.atTime(0,0,0,0));
    }

    // TODO: Get default Timezone and ZoneID
    public static TimeZone getZone() {
        return TimeZone.getDefault();
    }

    private static ZonedDateTime toZDT(LocalDateTime localDateTime) {
        System.out.println("Converting to Zoned Date Time.");
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
    }

    // TODO: Zoned Date Times to Eastern Time (Business Timezone) given LocalDateTime
    public static ZonedDateTime toEastZDT(LocalDateTime localDateTime) {
        return toZDT(localDateTime).withZoneSameInstant(ZoneId.of("US/Eastern"));
    }

    // TODO: Zoned Date Times to UTC Time (DB timezone) given LocalDateTime
    public static ZonedDateTime toUTCZDT(LocalDateTime localDateTime) {
        System.out.println("Retrieving converted time in ZDT.");
        return toZDT(localDateTime).withZoneSameInstant(ZoneId.of("UTC"));
    }

    /**
     * Conversion to Database time - UTC
     * @param localDateTime - time to be converted
     * @return converted time
     */
    public static LocalDateTime toUTC(LocalDateTime localDateTime) {
        System.out.println("Retrieving converted time in LDT form.");
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }

    /**
     * Conversion to Eastern Time for business hours
     * @param localDateTime - time to be converted
     * @return converted time
     */
    public static LocalDateTime toEast(LocalDateTime localDateTime) {
        System.out.println("Retrieving converted time in LDT form.");
        return toEastZDT(localDateTime).toLocalDateTime();
    }


    // FORMATTERS

    // TODO: Format Local Time
    public static String dateToString(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String dateToString(ZonedDateTime zonedDateTime, String pattern) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    // PARSERS

    // TODO: Read formatted date times
    public static LocalDateTime stringToLocal(String string) {
        return LocalDateTime.parse(string);
    }

    public static String getHour(LocalDateTime dt) {
        return dateToString(dt,"hh");
    }
    public static String getMinute(LocalDateTime dt) {
        return dateToString(dt,"mm");
    }
    public static String getMeridiem(LocalDateTime dt) {
        return dateToString(dt,"a").toUpperCase();
    }

    // TODO: Move to Loc.java and add a LocalDateTime.parse(str, formatter); DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")

    public static LocalDateTime formatTimeSelection(LocalDate date, int hour, int minute, String meridiem) {
        // Convert hour to 24-clock
        if (meridiem.equals("PM") && hour < 12) {
            hour = hour + 12 ;
        }

        LocalDateTime dateTime = date.atTime(hour,minute,0,0);
        System.out.println(dateTime);
        return dateTime;
    }

}
