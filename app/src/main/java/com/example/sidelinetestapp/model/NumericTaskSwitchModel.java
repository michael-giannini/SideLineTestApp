package com.example.sidelinetestapp.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.sidelinetestapp.standalone.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Class:		AntiSaccadeModel
Author:     Michael Giannini
Purpose:    This class contains the required data for the Numeric Task Switch test and is
            responsible for outputting a .csv file at the conclusion of the test.
*/
public class NumericTaskSwitchModel {

    private static final String LOG_TAG = NumericTaskSwitchModel.class.getSimpleName();

    public String participant;
    private Date date = new Date();
    private java.text.SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_kmm");
    private String strDate = formatter.format(date);

    public double[] magnitudeTime = new double[280];
    public double[] parityTime = new double[280];
    public double[] simpleTime = new double[280];
    public double[] stayTime = new double[280];
    public double[] switchTime = new double[280];

    private double meanMagTime;
    private double meanParityTime;
    private double meanSimpleTime;
    private double meanStayTime;
    private double meanSwitchTime;
    private double globalCost;
    private double switchCost;

    public int correctAnswers;
    public int incorrectAnswers;

    public long elapsedStartTime;
    public long elapsedEndTime;

    public int simpleLimit; //Should be 80 to match paper's method
    public int trialLimit; //Should be SIMPLE_COND_LIMIT + 160 = 240

    //Constructor. Elapsed start time is provided by the view model.
    public NumericTaskSwitchModel(String part, int simpleLimit, int trialLimit) {
        this.simpleLimit = simpleLimit;
        this.trialLimit = trialLimit;
        this.participant = part;
    }

    //Function: concludeTest
    //Description: When both tests have finished, calculate metrics and create output file.
    public void concludeTest() {
        calculateMeanTimes();
        calculateCosts();
        String output = generateResults();
        writeToFile(output);
        Log.d(LOG_TAG, "Ending Numeric Test, starting Spatial Activity.");
    }

    //Function: calculateMeanTimesw
    //Description: Find average value of an array
    private void calculateMeanTimes() {
        meanMagTime = arrayAverage(magnitudeTime);
        meanParityTime = arrayAverage(parityTime);
        meanSimpleTime = arrayAverage(simpleTime);
        meanStayTime = arrayAverage(stayTime);
        meanSwitchTime = arrayAverage(switchTime);
    }

    //Function: calculateCosts
    //Description: Calculate Costs of times
    private void calculateCosts() {
        globalCost = (meanStayTime - meanSimpleTime) / meanSimpleTime;
        switchCost = (meanSwitchTime - meanSimpleTime) / meanSimpleTime;
    }

    //Function: generateResults
    //Description: Generate csv string of results that will be saved to an output file.
    private String generateResults() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Participant: ," + participant);
        data.append("\nDate and Time: ," + String.valueOf(date));
        data.append("\nElapsed Time (s): ," + String.valueOf(Utility.nanotoSeconds(elapsedStartTime, elapsedEndTime)));
        data.append("\n ,Avg Magnitude Time (s),Avg Parity Time (s),Average Simple Time (s)," +
                "Average Stay Time (s),Average Switch Time (s),Global Cost, Switch Cost, Errors");
        data.append("\nResults," + String.valueOf(meanMagTime) + ","
                + String.valueOf(meanParityTime) + "," + String.valueOf(meanSimpleTime) + ","
                + String.valueOf(meanStayTime) + "," + String.valueOf(meanSwitchTime) + ","
                + String.valueOf(globalCost) + "," + String.valueOf(switchCost) + "," + String.valueOf(incorrectAnswers));
        data.append("\n\n,Magnitude Times (s),Parity Times (s),Simple Times(s),Stay Times(s),Switch Times(s)");
        for (int i = 0; i < trialLimit; i++) {
            data.append("\n," + String.valueOf(magnitudeTime[i]) + "," + String.valueOf(parityTime[i]) +
                    "," + String.valueOf(simpleTime[i]) + "," + String.valueOf(stayTime[i]) +
                    "," + String.valueOf(switchTime[i]));
        }
        return data.toString();
    }

    //Function: writeToFile
    //Description: Convert the passed array to a .csv file and save to /downloads folder of the device
    private void writeToFile(String data) {
        try {
            Log.d(LOG_TAG, "Trying to export.");
            //saving the file into device
            File root = android.os.Environment.getExternalStorageDirectory();
            File folder = new File(root.getAbsolutePath() + "/download");
            Log.d(LOG_TAG, "Dir: " + folder);
            File myFile = new File(folder, strDate + "_" + String.valueOf(participant) + "_NumericTest.csv");
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
        for (double i : array) {
            //Ignore times of 0 and times larger than 5s
            if (i > 0 && i < 5) {
                sum += i;
                numCount++;
            }
        }
        avg = sum / numCount;
        avg = Math.floor(avg * 1000) / 1000;
        return avg;
    }
}
