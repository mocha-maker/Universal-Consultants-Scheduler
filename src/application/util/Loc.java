package application.util;

import application.model.User;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
        return (toTimestamp(LocalDateTime.now()));
    }

    public static LocalDate getCurrentLocalDate() { return (LocalDateTime.now().toLocalDate()); }

    public static LocalTime getCurrentLocalTime() { return (LocalDateTime.now().toLocalTime()); }

    // CONVERSIONS

    // TODO: Convert java.sql.Date from database to LocalDateTime
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // TODO: Convert LocalDateTime to Timestamp for recording into database
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
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
        return toUTCZDT(localDateTime).toLocalDateTime();
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
    public static LocalDateTime toLocalDateTime(String string) {
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

    public static Timestamp getTimestamp(LocalDate date, String hour, String minute, String meridiem) {

        // Convert hour to 24-clock
        if (meridiem.equals("PM")) {
            hour = String.valueOf(Integer.parseInt(hour) + 12 );
        }

        Timestamp dateTime = Timestamp.valueOf(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + hour + ":" + minute + ":00 ");
        System.out.println(dateTime);
        return dateTime;
    }

}
