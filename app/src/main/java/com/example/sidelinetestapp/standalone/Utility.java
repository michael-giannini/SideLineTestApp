package com.example.sidelinetestapp.standalone;

import android.util.Log;

/*
Class:		Utility
Purpose:    This class contains methods that can be used across multiple classes.
*/
public class Utility {
    //Function: nanotoSeconds
    //Description: Converts two nanosecond values and returns the difference in seconds
    public static double nanotoSeconds(long startTime, long endTime) {
        double timeResult = (double) (endTime - startTime) / 1000000000;
        timeResult = Math.floor(timeResult * 1000) / 1000;
        return timeResult;
    }

    //Return system time
    public static long sysTime() {
        return System.nanoTime();
    }
}
