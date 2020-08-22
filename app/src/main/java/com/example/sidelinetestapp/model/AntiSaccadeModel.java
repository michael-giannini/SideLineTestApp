package com.example.sidelinetestapp.model;


import android.util.Log;

import com.example.sidelinetestapp.standalone.MainActivity;
import com.example.sidelinetestapp.standalone.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Class:		AntiSaccadeModel
Purpose:    This class contains the required data for the AntiSaccade test and is responsible for
            outputting a .csv file at the conclusion of the test.
*/
public class AntiSaccadeModel {
    private static final String LOG_TAG = AntiSaccadeModel.class.getSimpleName();

    private SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_kmm");

    public int TEST_STOP = 48; //According to the paper, this should be 48

    public String participant = "Unknown";
    private Date date = new Date();
    private String strDate = formatter.format(date);

    private long elapsedStartTime;
    private long elapsedEndTime;

    public double[] initiationTime = new double[48]; //holds all times
    public double[] movementTime = new double[48]; //holds all times
    private double[] avgInitiationTime = new double[2];
    private double[] avgMovementTime = new double[2];
    private double[] avgTotalTime = new double[2];

    private double[] proPointMoveTimes;
    private double[] proPointInitTimes;

    public int[] incorrectAnswers = new int[2]; //number of incorrect answers+
    public int completedTests; //number of completed tasks
    public int correctAnswers; //number of correct answers

    //Constructor. Elapsed start time is provided by the view model.
    public AntiSaccadeModel(long elapsedStartTime) {
        this.elapsedStartTime = elapsedStartTime;
    }


    //Function: concludeTest
    //Description: When a test is complete, calculate average times.
    public void concludeTest() {
        avgInitiationTime[completedTests] = arrayAverage(initiationTime);
        avgMovementTime[completedTests] = arrayAverage(movementTime);
        avgTotalTime[completedTests] = avgInitiationTime[completedTests] + avgMovementTime[completedTests];
        completedTests++;
        correctAnswers = 0;
        startSecondTest();
    }

    //Function: startSecondTest
    //Description: If only one test has been conducted, switch to AntiPoint Test.
    //             Else, save results to CSV and return to tests menu.
    private void startSecondTest() {
        if (completedTests == 1) {
            proPointInitTimes = initiationTime;
            proPointMoveTimes = movementTime;
        } else {
            elapsedEndTime = System.nanoTime();
            completedTests = 0;
            //Exit activity and save data
            String results = generateResults();
            writeToFile(results);
        }
    }

    //Function: generateResults
    //Description: Create CSV string of results
    private String generateResults() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Participant: ," + participant);
        data.append("\nDate and Time: ," + String.valueOf(date));
        data.append("\nElapsed Time (s): ," + String.valueOf(Utility.nanotoSeconds(elapsedStartTime, elapsedEndTime)));
        data.append("\n ,Avg Initiation Time (s), Avg Movement Time (s), Average Total Time (s), Errors");
        data.append("\nProPoint," + String.valueOf(avgInitiationTime[0]) + ","
                + String.valueOf(avgMovementTime[0]) + "," + String.valueOf(avgTotalTime[0]) + ","
                + String.valueOf(incorrectAnswers[0]));
        data.append("\nAntiPoint," + String.valueOf(avgInitiationTime[1]) + ","
                + String.valueOf(avgMovementTime[1]) + "," + String.valueOf(avgTotalTime[1]) + ","
                + String.valueOf(incorrectAnswers[1]));
        data.append("\n\n,ProPoint Initiation Times (s),ProPoint Movement Times (s)," +
                "AntiPoint Initiation Times(s),AntiPoint Movement Times(s)");
        for (int i = 0; i < TEST_STOP; i++) {
            data.append("\n," + String.valueOf(proPointInitTimes[i]) + "," + String.valueOf(proPointMoveTimes[i]) +
                    "," + String.valueOf(initiationTime[i]) + "," + String.valueOf(movementTime[i]));
        }
        return data.toString();
    }

    //Function: writeToFile
    //Description:Write to a CSV file
    private void writeToFile(String data) {
        try {
            Log.d(LOG_TAG, "Trying to export.");
            //saving the file into device
            File root = android.os.Environment.getExternalStorageDirectory();
            File folder = new File(root.getAbsolutePath() + "/download");
            Log.d(LOG_TAG, "Dir: " + folder);
            File myFile = new File(folder, strDate + "_" + String.valueOf(participant) + "_SaccadeTest.csv");
            FileOutputStream out = new FileOutputStream(myFile);
            out.write(data.getBytes());
            out.close();
            Log.d(LOG_TAG, "Save Successful.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Exception occurred while trying to export.");
        }
    }

    //Function: arrayAverage
    //Description: Find average value of an array
    private double arrayAverage(double[] array) {
        double avg;
        double sum = 0;
        int numCount = 0;
        for (double i : array)
            if (i > 0) {
                sum += i;
                numCount++;
            }
        avg = sum / numCount;
        avg = Math.floor(avg * 1000) / 1000;
        return avg;
    }

}
