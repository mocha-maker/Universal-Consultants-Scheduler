package application.util;

import java.util.Locale;
import java.util.Scanner;

public abstract class Tests {

    public void overrideSystemDefaults() {
        testTimeZone();
        testLang();
    }

    public void testTimeZone() {
        String timezone = newScanner("Enter a timezone E.g. America/Los_Angeles");
        System.setProperty("user.timezone", timezone);
    }

    public void testLang()  {
        String lang = newScanner("Enter language package to use (1 = English 2 = French)");

        switch(lang) {
            case "2":
                Locale.setDefault(new Locale("fr", "CA"));
                break;
            case "1":
                break;
            default:
                Locale.setDefault(new Locale("en", "US"));
        }
    }

    public String newScanner(String msg) {
        Scanner sel = new Scanner(System.in);
        System.out.println(msg);
        return sel.nextLine();
    }

// end of class
}
