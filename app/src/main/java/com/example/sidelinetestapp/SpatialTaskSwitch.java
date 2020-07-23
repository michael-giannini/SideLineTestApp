package com.example.sidelinetestapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class SpatialTaskSwitch extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static int SIMPLE_COND_LIMIT; //Should be 80 to match paper's method
    private static int TRIAL_LIMIT; //Should be SIMPLE_COND_LIMIT + 160 = 240

    private Button leftClick;
    private Button rightClick;
    private Button leftClickMinus;
    private Button rightClickPlus;
    private Button startButton;
    private ImageView topSquare;
    private ImageView bottomSquare;
    private ImageView divider;

    private long elapsedStartTime;
    private long elapsedEndTime;
    private long startTime;
    private long endTime;

    private double[] congruentTime;
    private double[] incongruentTime;
    private double[] simpleTime;
    private double[] stayTime;
    private double[] switchTime;

    private int correctAnswers;
    private int incorrectAnswers;
    private int switchCount;

    private int conIndex;
    private int inconIndex;
    private int stayIndex;
    private int switchIndex;

    private boolean activeTop;
    private boolean activeBottom;
    private boolean congruent;

    private String strDate;
    private String participant = "Unknown";

    private double meanSwitchTime;
    private double meanConTime;
    private double meanInconTime;
    private double meanSimpleTime;
    private double meanStayTime;
    private double switchCost;
    private double globalCost;

    private Date date;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Starting Spatial Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spatial_task_switch);

        date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_kmm");
        strDate = formatter.format(date);
        Intent intent = getIntent();
        participant = intent.getStringExtra(saccadeInstructions.EXTRA_MESSAGE);
        startButton = (Button) findViewById(R.id.taskSwitchStartButton);
        leftClick = (Button) findViewById(R.id.taskSwitchLeftButton);
        rightClick = (Button) findViewById(R.id.taskSwitchRightButton);
        leftClickMinus = (Button) findViewById(R.id.taskSwitchLeftButtonMinus);
        rightClickPlus = (Button) findViewById(R.id.taskSwitchRightButtonPlus);
        topSquare = (ImageView) findViewById(R.id.topSquare);
        bottomSquare = (ImageView) findViewById(R.id.bottomSquare);
        divider = (ImageView) findViewById(R.id.divider);
        Log.d(LOG_TAG, "Started Spatial Activity");
        congruent = true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String simpleTrials = sharedPref.getString("simple_trial_number", "80");
        String totalTrials = sharedPref.getString("total_trial_number", "260");
        SIMPLE_COND_LIMIT = Integer.parseInt(simpleTrials);
        TRIAL_LIMIT = Integer.parseInt(totalTrials);
        congruentTime = new double[TRIAL_LIMIT];
        incongruentTime = new double[TRIAL_LIMIT];
        simpleTime = new double[TRIAL_LIMIT];
        stayTime = new double[TRIAL_LIMIT];
        switchTime = new double[TRIAL_LIMIT];
    }


    public void startSpatialTest(View view) {
        elapsedStartTime = sysTime();
        startButton.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.VISIBLE);
        displayRandomSquare();
        leftClick.setEnabled(true);
        rightClick.setEnabled(true);
    }

    private void displayRandomSquare() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean activeSquare = Math.random() < 0.5;
                if (activeSquare) {
                    topSquare.setVisibility(View.VISIBLE);
                    activeTop = true;
                    activeBottom = false;
                } else {
                    bottomSquare.setVisibility(View.VISIBLE);
                    activeBottom = true;
                    activeTop = false;
                }
                startTime = sysTime();
            }
        }, 100);
    }

    //Function: leftButtonClick
    //Description:
    public void leftButtonClick(View view) {
        if (activeBottom) correctAnswer();
        else incorrectAnswer();
    }


    //Function: rightButtonClick
    //Description:
    public void rightButtonClick(View view) {
        if (activeTop) correctAnswer();
        else incorrectAnswer();
    }

    private void correctAnswer() {
        //displayToast("Correct");
        logTime();
        correctAnswers++;
        //If trial limit is reached, end test
        if (correctAnswers == TRIAL_LIMIT) concludeTest();
        //If we are out of the simple limit increment switch count
        if (correctAnswers > SIMPLE_COND_LIMIT) {
            switchCount++;
            //Switch tasks when switchCount ==2
            if (switchCount == 2) taskSwitch();
        }
        bottomSquare.setVisibility(View.INVISIBLE);
        topSquare.setVisibility(View.INVISIBLE);
        displayRandomSquare();
    }

    private void taskSwitch() {
        Log.d(LOG_TAG, "Switching Task");
        if (congruent) {
            leftClick.setEnabled(false);
            rightClick.setEnabled(false);
            leftClickMinus.setEnabled(true);
            rightClickPlus.setEnabled(true);
        } else {
            leftClick.setEnabled(true);
            rightClick.setEnabled(true);
            leftClickMinus.setEnabled(false);
            rightClickPlus.setEnabled(false);
        }
        congruent = !congruent;
        switchCount = 0;
    }

    private void concludeTest() {
        elapsedEndTime = sysTime();
        calculateMeanTimes();
        calculateCosts();
        String output = generateResults();
        writeToFile(output);
        displayToast("DONE");
        displayDialogue();
    }

    private void displayDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Complete");
        builder.setMessage("Thank you for completing the tests, you will now be returned to the main menu.");
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchActivities();
                        dialog.cancel();
                    }
                });
        AlertDialog endOfTestAlert = builder.create();
        endOfTestAlert.show();
    }

    private void switchActivities() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void calculateMeanTimes() {
        meanConTime = arrayAverage(congruentTime);
        meanInconTime = arrayAverage(incongruentTime);
        meanSimpleTime = arrayAverage(simpleTime);
        meanStayTime = arrayAverage(stayTime);
        meanSwitchTime = arrayAverage(switchTime);
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

    private void calculateCosts() {
        globalCost = (meanStayTime - meanSimpleTime) / meanSimpleTime;
        switchCost = (meanSwitchTime - meanSimpleTime) / meanSimpleTime;
    }

    private String generateResults() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Participant: ," + participant);
        data.append("\nDate and Time: ," + String.valueOf(date));
        data.append("\nElapsed Time (s): ," + String.valueOf(nanotoSeconds(elapsedStartTime, elapsedEndTime)));
        data.append("\n ,Avg Congruent Time (s), Avg Incongruent Time (s), Average Simple Time (s), " +
                "Average Stay Time (s), Average Switch Time (s), Global Cost, Switch Cost, Error Rate");
        data.append("\nResults," + String.valueOf(meanConTime) + ","
                + String.valueOf(meanInconTime) + "," + String.valueOf(meanSimpleTime) + ","
                + String.valueOf(meanStayTime) + "," + String.valueOf(meanSwitchTime) + ","
                + String.valueOf(globalCost) + "," + String.valueOf(switchCost) + "," + String.valueOf(incorrectAnswers / TRIAL_LIMIT));
        data.append("\n\n,Congruent Times (s),Incongruent Times (s),Simple Times(s),Stay Times(s),Switch Times(s)");
        for (int i = 0; i < TRIAL_LIMIT; i++) {
            data.append("\n," + String.valueOf(congruentTime[i]) + "," + String.valueOf(incongruentTime[i]) +
                    "," + String.valueOf(simpleTime[i]) + "," + String.valueOf(stayTime[i]) +
                    "," + String.valueOf(switchTime[i]));
        }
        return data.toString();
    }

    private void logTime() {
        long endTime = sysTime();
        //Store reaction time for simple trials
        if (correctAnswers < SIMPLE_COND_LIMIT) {
            simpleTime[correctAnswers] = nanotoSeconds(startTime, endTime);
        }
        //Store reaction time in either Magnitude Array or Parity Array
        if (congruent) {
            Log.d(LOG_TAG, "recording congruent time.");
            congruentTime[conIndex] = nanotoSeconds(startTime, endTime);
            conIndex++;
        } else {
            Log.d(LOG_TAG, "recording incongruent time.");
            incongruentTime[inconIndex] = nanotoSeconds(startTime, endTime);
            inconIndex++;
        }
        //Store Reaction time in either stay or switch array
        if (switchCount == 1) {
            Log.d(LOG_TAG, "recording stay time.");
            stayTime[stayIndex] = nanotoSeconds(startTime, endTime);
            stayIndex++;
        } else if (switchCount == 0 && correctAnswers > SIMPLE_COND_LIMIT) {
            Log.d(LOG_TAG, "recording switch time.");
            switchTime[switchIndex] = nanotoSeconds(startTime, endTime);
            switchIndex++;
        }
    }

    private void incorrectAnswer() {
        //displayToast("Incorrect");
        incorrectAnswers++;
    }


    //Return system time
    private long sysTime() {
        Log.d(LOG_TAG, "Collected system time.");
        return System.nanoTime();
    }

    //Display a toast
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }

    private double nanotoSeconds(long startTime, long endTime) {
        double timeResult = (double) (endTime - startTime) / 1000000000;
        timeResult = Math.floor(timeResult * 1000) / 1000;
        return timeResult;
    }
}
