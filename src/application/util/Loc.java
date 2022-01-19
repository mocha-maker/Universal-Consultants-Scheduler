package application.util;

import application.model.Appointment;

import java.sql.Time;
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

    public static Timestamp getCurrentTime() {
        return (toTimestamp(LocalDateTime.now()));
    }

    // TODO: Convert java.sql.Date from database to LocalDateTime
    public static LocalDateTime getLocalDateTime(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime;
    }

    // TODO: Convert LocalDateTime to Timestamp for recording into database
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        
        return timestamp;
    }

    // TODO: Convert LocalDateTime to LocalDate
    public static LocalDate toLocalDate(LocalDateTime localDateTime) {
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    // TODO: Convert LocalDateTime to LocalTime
    public static LocalTime toLocalTime(LocalDateTime localDateTime) {
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }


    // TODO: Get default Timezone and ZoneID
    public static TimeZone getZone() {
        TimeZone zone = TimeZone.getDefault();
        return zone;
    }
    public static ZoneId getZoneId() {
        ZoneId zoneId = TimeZone.getDefault().toZoneId();
        return zoneId;
    }

    // TODO: Zoned Date Times to Eastern Time (Business Timezone) given LocalDateTime
    public static ZonedDateTime toEastZDT(ZonedDateTime zonedDateTime) {
        ZonedDateTime eastZDT = toUTCZDT(zonedDateTime).withZoneSameInstant(ZoneId.of("US/Eastern"));
        return eastZDT;
    }

    // TODO: Zoned Date Times to UTC Time (DB timezone) given LocalDateTime
    public static ZonedDateTime toUTCZDT(ZonedDateTime zonedDateTime) {
        Instant instantUTC = zonedDateTime.toInstant();
        ZonedDateTime zoneUTC = instantUTC.atZone(ZoneId.of("UTC"));
        return zoneUTC;
    }
    
    // TODO: Format Local Time
    public static String dateTimeFormatter(LocalDateTime localDateTime, String pattern) {
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern(pattern));
        return formattedDateTime;
    }

    public static String eastTime(ZonedDateTime zdt) {
        String time = dateToString(toEastZDT(zdt), "hh:mm:ss");
        return time;
    }

    public static String UTCTime(ZonedDateTime zdt) {
        String time = dateToString(toUTCZDT(zdt),"hh:mm:ss");
        return time;
    }


    public static String dateToString(ZonedDateTime toUTCZDT, String pattern) {
        String formatDateTime = toUTCZDT.format(DateTimeFormatter.ofPattern(pattern));
        return formatDateTime;
    }

    public static String dateToString(LocalDateTime dateTime, String pattern) {
        String formatDateTime = dateTime.format(DateTimeFormatter.ofPattern(pattern));
        return formatDateTime;
    }

    // TODO: Read formatted date times
    public static LocalDateTime toLocalDateTime(String string) {
        LocalDateTime localDateTime = LocalDateTime.parse(string);
        return localDateTime;
    }

    public static String getHour(LocalDateTime dt) {
        return dateTimeFormatter(dt,"hh");
    }
    public static String getMinute(LocalDateTime dt) {
        return dateTimeFormatter(dt,"mm");
    }
    public static String getMeridiem(LocalDateTime dt) {
        return dateTimeFormatter(dt,"a").toUpperCase();
    }

    // TODO: Move to Loc.java and add a LocalDateTime.parse(str, formatter); DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")

    public static Timestamp toTimestamp(LocalDate date, String hour, String minute, String meridiem) {

        if (meridiem=="PM") {
            hour = String.valueOf(Integer.parseInt(hour) + 12 );
        }
        Timestamp dateTime = Timestamp.valueOf(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + hour + ":" + minute + ":00");
        System.out.println(dateTime);
        return dateTime;
    }

}
