package application.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateTime {

    // Get Timezone and ZoneID
    public static TimeZone getTimeZone() {
        TimeZone zone = TimeZone.getDefault();
        return zone;
    }

    public void toLocalDate(){

    }

    public void toLocalTime(){

    }

    public void DateTimeFormatter(){

    }

    public static String ConvertTime(ZonedDateTime zonedTime) {
        String convertedTime = zonedTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        System.out.println("12H time: " + convertedTime);
        return convertedTime;
    }

    public static void TimeDate() {
        // Timezone App

        LocalDate parisDate = LocalDate.of(2021,11,19);
        LocalTime parisTime = LocalTime.of(05, 00);
        ZoneId parisZoneID = ZoneId.of("Europe/Paris");

        ZonedDateTime parisZDT = ZonedDateTime.of(parisDate, parisTime, parisZoneID);
        ZoneId localZoneId = ZoneId.of(TimeZone.getDefault().getID());
        System.out.println("Local Zone ID: " + localZoneId);

        Instant parisToUTCInstant = parisZDT.toInstant();
        ZonedDateTime parisInstantToZDT = parisToUTCInstant.atZone(ZoneId.of("UTC"));
        ZonedDateTime parisToLocalZDT = parisZDT.withZoneSameInstant(localZoneId);
        ZonedDateTime UTCToLocalZDT = parisToUTCInstant.atZone(localZoneId);

        //System.out.println("Local: " + ZonedDateTime.now());
        System.out.println("Paris: " + parisZDT);

        System.out.println("Paris -> Local: " + parisToLocalZDT);
        System.out.println("Paris -> UTC: " + parisToUTCInstant);
        System.out.println("Paris -> UTC: " + parisInstantToZDT.toLocalTime());
        System.out.println("UTC -> Local: " + UTCToLocalZDT.toLocalDate());
    }
}
