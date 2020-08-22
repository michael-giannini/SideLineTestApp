package com.example.sidelinetestapp.model;

import android.util.Log;

import com.example.sidelinetestapp.standalone.MainActivity;
import com.example.sidelinetestapp.standalone.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SpatialTaskSwitchModel {
    private static final String LOG_TAG = SpatialTaskSwitchModel.class.getSimpleName();
    public final int trialLimit;
    public final int simpleLimit;

    public String participant;
    private Date date = new Date();
    private java.text.SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_kmm");
    private String strDate = formatter.format(date);

    public long elapsedStartTime;
    public long elapsedEndTime;

    public double[] congruentTime = new double[280];
    public double[] incongruentTime = new double[280];
    public double[] simpleTime = new double[280];
    public double[] stayTime = new double[280];
    public double[] switchTime = new double[280];

    public int correctAnswers;
    public int incorrectAnswers;

    private double meanSwitchTime;
    private double meanConTime;
    private double meanInconTime;
    private double meanSimpleTime;
    private double meanStayTime;
    private double switchCost;
    private double globalCost;

    //Constructor. Elapsed start time is provided by the view model.
    public SpatialTaskSwitchModel(String part, int simpleLimit, int trialLimit) {
        this.simpleLimit = simpleLimit;
        this.trialLimit = trialLimit;
        this.participant = part;
    }

    public void concludeTest() {
        calculateMeanTimes();
        calculateCosts();
        String output = generateResults();
        writeToFile(output);
    }

    private void calculateCosts() {
        globalCost = (meanStayTime - meanSimpleTime) / meanSimpleTime;
        switchCost = (meanSwitchTime - meanSimpleTime) / meanSimpleTime;
    }

    private void calculateMeanTimes() {
        meanConTime = arrayAverage(congruentTime);
        meanInconTime = arrayAverage(incongruentTime);
        meanSimpleTime = arrayAverage(simpleTime);
        meanStayTime = arrayAverage(stayTime);
        meanSwitchTime = arrayAverage(switchTime);
    }

    private void writeToFile(String data) {
        try {
            Log.d(LOG_TAG, "Trying to export.");
            //saving the file into device
            File root = android.os.Environment.getExternalStorageDirectory();
            File folder = new File(root.getAbsolutePath() + "/download");
            Log.d(LOG_TAG, "Dir: " + folder);
            File myFile = new File(folder, strDate + "_" + String.valueOf(participant) + "_SpatialTest.csv");
            FileOutputStream out = new FileOutputStream(myFile);
            out.write(data.getBytes());
            out.close();
            Log.d(LOG_TAG, "Save Successful.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Exception occurred while trying to export.");
        }
    }

    private String generateResults() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Participant: ," + participant);
        data.append("\nDate and Time: ," + String.valueOf(date));
        data.append("\nElapsed Time (s): ," + String.valueOf(Utility.nanotoSeconds(elapsedStartTime, elapsedEndTime)));
        data.append("\n ,Avg Congruent Time (s), Avg Incongruent Time (s), Average Simple Time (s), " +
                "Average Stay Time (s), Average Switch Time (s), Global Cost, Switch Cost, Errors");
        data.append("\nResults," + String.valueOf(meanConTime) + ","
                + String.valueOf(meanInconTime) + "," + String.valueOf(meanSimpleTime) + ","
                + String.valueOf(meanStayTime) + "," + String.valueOf(meanSwitchTime) + ","
                + String.valueOf(globalCost) + "," + String.valueOf(switchCost) + "," + String.valueOf(incorrectAnswers));
        data.append("\n\n,Congruent Times (s),Incongruent Times (s),Simple Times(s),Stay Times(s),Switch Times(s)");
        for (int i = 0; i < trialLimit; i++) {
            data.append("\n," + String.valueOf(congruentTime[i]) + "," + String.valueOf(incongruentTime[i]) +
                    "," + String.valueOf(simpleTime[i]) + "," + String.valueOf(stayTime[i]) +
                    "," + String.valueOf(switchTime[i]));
        }
        return data.toString();
    }

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
