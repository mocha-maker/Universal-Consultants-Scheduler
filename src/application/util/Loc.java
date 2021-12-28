package application.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Localization Helper Class
 */
public abstract class Loc {
    static ResourceBundle rb;
    static Locale activeLocale;


    // Locale Management //

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

    // Get Timezone and ZoneID
    public static TimeZone getZone() {
        TimeZone zone = TimeZone.getDefault();
        return zone;
    }

    // TODO: Get local time and dates
    public static LocalDateTime getLocalDateTime() {
        LocalDateTime localDT = LocalDateTime.now();
        return localDT;
    }


    public static ZonedDateTime toEastZDT(ZonedDateTime zonedDateTime) {
        ZonedDateTime eastZDT = toUTCZDT(zonedDateTime).withZoneSameInstant(ZoneId.of("US/Eastern"));
        return eastZDT;
    }
    
    public static ZonedDateTime toUTCZDT(ZonedDateTime zonedDateTime) {
        Instant instantUTC = zonedDateTime.toInstant();
        ZonedDateTime zoneUTC = instantUTC.atZone(ZoneId.of("UTC"));
        return zoneUTC;
    }
    

    public static String timeFormatter(ZonedDateTime zonedTime) {
        String formatTime = zonedTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        return formatTime;
    }

    public static String eastTime(ZonedDateTime zdt) {
        String time = timeFormatter(toEastZDT(zdt));
        return time;
    }

    public static String UTCTime(ZonedDateTime zdt) {
        String time = timeFormatter(toUTCZDT(zdt));
        return time;
    }

}
