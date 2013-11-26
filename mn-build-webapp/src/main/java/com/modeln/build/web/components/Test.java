package com.modeln.build.web.components;

import java.util.GregorianCalendar;

public class Test {

    /**
     * The main method is used to test the class from the command line to
     * ensure that perforce output is being parsed correctly.
     */
    public static void main(String[] args) {
        try {
            GregorianCalendar start = new GregorianCalendar(2004, GregorianCalendar.MARCH, 10);
            GregorianCalendar end   = new GregorianCalendar(2004, GregorianCalendar.MARCH, 25);

            Calendar calendar = new Calendar(start, end);
            calendar.setTitle("Test");
            System.out.println(calendar.toString());

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

}

