/*
 * RandomData.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.testing;


import java.util.*;
import java.awt.Color;

/**
 * This class generates random data values.
 *
 * @author  Shawn Stafford (sstafford@modeln.com)
 * @version 1.0 (Last Modified: April 10, 2002)
 *
 */
public class RandomData {

    public static final String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String[] maleFirstNames = {
        "Abe", "Adam", "Albert", "Bill", "Charles", 
        "David", "Donald", "Fred", "Frank", "George", 
        "Henry", "James", "John", "Kevin", "Kyle",
        "Larry", "Lawrence", "Mike", "Matt", "Peter",
        "Richard", "Steve", "Ted", "Vince", "Walter"
    };

    public static String[] femaleFirstNames = {
        "Alice", "Anita", "Barbera", "Betty", "Christine",
        "Dianne", "Donna", "Doris", "Elizabeth", "Jennifer", 
        "Julie", "Kathy", "Laura", "Lucy", "Marge", "Michelle",
        "Natalie", "Paula", "Roberta", "Sera", "Tammy"
    };

    public static String[] lastNames = {
        "Adams", "Alvarez", "Anderson", "Armani", "Bennett", "Burns", 
        "Carlson", "Davidson", "Donalds", "DuWitt", "Dumas",
        "Edwards", "Emerson", "Ericson", "Franks", "Griswald",
        "Howard", "Jones", "Jefferson", "Kline", "Long", "Michaels",
        "Norton", "Ormand", "Petterson", "Pruit", "Richards", "Stafford",
        "Stevens", "Walton", "Watts", "Zimmerman"
    };

    /**
     * Construct the random data generator.
     */
    private RandomData() {
    }

    /**
     * Returns a random first name.
     *
     * @param   male    TRUE if the name should be male, FALSE for female
     */
    public static String getRandomFirstname(boolean male) {
        if (male) {
            return getRandomString(maleFirstNames);
        } else {
            return getRandomString(femaleFirstNames);
        }
    }

    /**
     * Returns a random last name.
     */
    public static String getRandomLastname() {
        return getRandomString(lastNames);
    }

    /**
     * Returns a random string from the array of strings.
     *
     * @param   list    Array of strings to select from
     * @return  String selected from the array
     */
    public static String getRandomString(String[] list) {
        int idx = getRandomInt(0, list.length - 1);
        return list[idx];
    }

    /**
     * Returns a random string of characters [a-Z] of a random
     * length between the specified minimum and maximum length.
     *
     * @param   min     minimum length of the string
     * @param   max     maximum length of the string
     * @return  Random string
     */
    public static String getRandomString(int min, int max) {
        String str = "";
        int size = getRandomInt(min, max);

        int charIdx = 0;
        for (int idx = 0; idx <= size; idx++) {
            charIdx = (int)((ALPHA.length() - 1) * Math.random());
            str = str + ALPHA.charAt(charIdx);
        }

        return str;
    }

    /**
     * Returns a random date within the given year.
     *
     * @param   year    Year to select the date in
     * @return  GregorianCalendar representing the random date
     */
    public static GregorianCalendar getRandomDate(int year) {
        int month = getRandomInt(Calendar.JANUARY, Calendar.DECEMBER);
        int day = getRandomInt(1, 28);

        return new GregorianCalendar(year, month, day);
    }

    /**
     * Returns a random date within the given range of years.
     *
     * @param   begin   beginning range of years
     * @param   end     ending range of years
     * @return  GregorianCalendar representing the random date
     */
    public static GregorianCalendar getRandomDate(int begin, int end) {
        int year = getRandomInt(begin, end);
        int month = getRandomInt(Calendar.JANUARY, Calendar.DECEMBER);
        int day = getRandomInt(1, 28);

        return new GregorianCalendar(year, month, day);
    }

    /**
     * Returns a random date within the given range of dates.
     *
     * @param   begin   beginning date
     * @param   end     ending date
     * @return  GregorianCalendar representing the random date
     */
    public static GregorianCalendar getRandomDate(Date begin, Date end) {
        long time = getRandomLong(end.getTime(), begin.getTime());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(time));
        return calendar;
    }

    /** 
     * Returns a random integer within the given range of values.
     *
     * @param   min     minimum value
     * @param   max     maximum value
     * @return random integer value
     */
    public static int getRandomInt(int min, int max) {
        return min + (int)((max - min) * Math.random());
    }

    /** 
     * Returns a random long integer within the given range of values.
     *
     * @param   min     minimum value
     * @param   max     maximum value
     * @return random integer value
     */
    public static long getRandomLong(long min, long max) {
        return min + (long)((max - min) * Math.random());
    }

    /** 
     * Returns a random float within the given range of values.
     *
     * @param   min     minimum value
     * @param   max     maximum value
     * @return random float value
     */
    public static float getRandomFloat(float min, float max) {
        return min + (float)((max - min) * Math.random());
    }

    /** 
     * Returns a random double value within the given range of values.
     *
     * @param   min     minimum value
     * @param   max     maximum value
     * @return random double value
     */
    public static double getRandomDouble(double min, double max) {
        return min + ((max - min) * Math.random());
    }

    /**
     * Returns a random color value.
     */
    public static Color getRandomColor() {
        int red = (int)(255 * Math.random());
        int green = (int)(255 * Math.random());
        int blue = (int)(255 * Math.random());
        return new Color(red, green, blue);
    }

    /**
     * Returns a random boolean value.
     */
    public static boolean getRandomBoolean() {
        if (Math.random() > 0.5) {
            return true;
        } else {
            return false;
        }
    }

}
