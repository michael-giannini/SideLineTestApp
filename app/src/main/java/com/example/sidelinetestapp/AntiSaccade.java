package com.example.sidelinetestapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.EventLogTags;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AntiSaccade extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int TEST_STOP = 14; //According to the paper, this should be 48
    private static final int NUM_TARGETS = 4; //Only change if more targets are added

    private ImageView[] target = new ImageView[NUM_TARGETS];
    private ImageView[] arrow = new ImageView[NUM_TARGETS];
    private TextView mShowCount;
    private TextView mInitTime;
    private TextView mMoveTime;

    private int newTarget; //indicates which square should be visible
    private int arrowTarget; //indicates which arrow should be visible
    private int correctTarget; //indicates the correct target
    private int correctAnswers; //number of correct answers
    private int[] incorrectAnswers = new int[2]; //number of incorrect answers+
    private int completedTests; //number of completed tasks

    private long startTime; //timer start
    private long endTime; ///timer end
    private long elapsedStartTime;
    private long elapsedEndTime;

    private double[] initiationTime = new double[TEST_STOP]; //holds all times
    private double[] movementTime = new double[TEST_STOP]; //holds all times
    private double[] avgInitiationTime = new double[2];
    private double[] avgMovementTime = new double[2];
    private double[] avgTotalTime = new double[2];

    private String participant = "Unknown";
    private String strDate;

    private Date date;

    private boolean activeTarget; //indicates if there is an active target waiting to be pressed
    private boolean AntiPoint; //Indicates if it is a Pro-Point test or Anti-Point
    private double[] proPointMoveTimes;
    private double[] proPointInitTimes;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "-------");
        Log.d(LOG_TAG, "Anti Saccade Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_saccade);
        date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_kmm");
        strDate = formatter.format(date);
        Intent intent = getIntent();
        participant = intent.getStringExtra(saccadeInstructions.EXTRA_MESSAGE);

        //Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Permission is granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Permission is granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //Assign each image view to its array
        for (int i = 0; i < NUM_TARGETS; i++) {
            int res = getResources().getIdentifier("target" + i, "id", getPackageName());
            int arrowRes = getResources().getIdentifier("arrow" + i, "id", getPackageName());
            target[i] = findViewById(res);
            arrow[i] = findViewById(arrowRes);
        }

        mShowCount = (TextView) findViewById(R.id.saccadeCounter);
        mInitTime = (TextView) findViewById(R.id.initTimeValueTV);
        mMoveTime = (TextView) findViewById(R.id.movementTimeValueTV);
        ImageView mStartCircle = (ImageView) findViewById(R.id.startCircle);
        mStartCircle.setOnLongClickListener(startCircleLongClick);
        mStartCircle.setOnTouchListener(actionUp);
        completedTests = 0;
        activeTarget = false;
        AntiPoint = false; //randomBoolean();
        elapsedStartTime = System.nanoTime();
    }

    //Execute when long click on start circle happens
    private View.OnLongClickListener startCircleLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.d(LOG_TAG, "Long Click Occurred.");
            startAntiSaccadeTest();
            return true;
        }
    };

    private final View.OnTouchListener actionUp = new View.OnTouchListener() {
        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && activeTarget) {
                //Get timestamp for initiation time
                endTimer();
                initiationTime[correctAnswers] = nanotoSeconds(startTime, endTime);
                mInitTime.setText(Double.toString(initiationTime[correctAnswers]));
                Log.d(LOG_TAG, "Initiation Time: " + initiationTime[correctAnswers]);
                //Start new time for movement time
                startTimer();
            }
            return false;
        }
    };

    //Function: startAntiSaccadeTest
    //Description: If there is no active target, randomly choose a square to be the target
    public void startAntiSaccadeTest() {
        if (!activeTarget) {
            Log.d(LOG_TAG, "Start circle touched");
            //choose a random square to become the target
            arrowTarget = newTarget = correctTarget = (int) (Math.random() * ((NUM_TARGETS - 1) + 1) + 0);
            //change chosen arrow to be a visible
            if (AntiPoint) {
                switch (newTarget) {
                    case 0:
                        arrowTarget = correctTarget = 2;
                        break;
                    case 1:
                        arrowTarget = correctTarget = 3;
                        break;
                    case 2:
                        arrowTarget = correctTarget = 0;
                        break;
                    case 3:
                        arrowTarget = correctTarget = 1;
                        break;
                }
            }
            arrow[arrowTarget].setVisibility(View.VISIBLE);
            target[newTarget].setImageResource(R.drawable.targetsquare);
            startTimer();
            activeTarget = true;
            Log.d(LOG_TAG, "Square chosen: " + newTarget);
        } else {
            displayToast("Press the filled square");
        }
    }

    //Function: targetTouched
    //Description: When a square is touched, determine if it is the correct square. If it is the
    //correct square, record times
    @SuppressLint("SetTextI18n")
    public void targetTouched(View view) {
        //Determine if there is an active target
        if (activeTarget) {
            Log.d(LOG_TAG, "Target Touched");
            //Read which square was touched
            String clickedTarget = view.getTag().toString();
            Log.d(LOG_TAG, "Square: " + clickedTarget);

            //Determine if correct square was touched
            if (clickedTarget.equals(Integer.toString(correctTarget))) {
                endTimer();
                Log.d(LOG_TAG, "Correct Target Clicked");
                movementTime[correctAnswers] = nanotoSeconds(startTime, endTime);
                mMoveTime.setText(Double.toString(movementTime[correctAnswers]));
                Log.d(LOG_TAG, "Movement Time: " + movementTime[correctAnswers]);
                resetTargets(newTarget, arrowTarget);
                correctAnswers++;
                //Start 2nd half of Anti-Saccade test
                if (correctAnswers == TEST_STOP) concludeTest();

            } else {
                Log.d(LOG_TAG, "Incorrect Target Clicked");
                resetTargets(newTarget, arrowTarget);
                incorrectAnswers[completedTests]++;
            }
        }
    }

    //Function: resetTargets
    //Description: Remove active targets from the screen
    private void resetTargets(int currentTarget, int currentArrow) {
        target[currentTarget].setImageResource(R.drawable.emptysquare);
        arrow[currentArrow].setVisibility(View.INVISIBLE);
        activeTarget = false;
    }

    //Function: concludeTest
    //Description: When a test is complete, calculate average times.
    @SuppressLint("SetTextI18n")
    private void concludeTest() {
        avgInitiationTime[completedTests] = arrayAverage(initiationTime);
        avgMovementTime[completedTests] = arrayAverage(movementTime);
        avgTotalTime[completedTests] = avgInitiationTime[completedTests] + avgMovementTime[completedTests];
        completedTests++;
        correctAnswers = 0;
        mShowCount.setText(Integer.toString(correctAnswers) + "/" + TEST_STOP);
        startSecondTest();
    }

    //Function: startSecondTest
    //Description: If only one test has been conducted, switch to AntiPoint Test.
    //             Else, save results to CSV and return to tests menu.
    private void startSecondTest() {
        if (completedTests == 1) {
            proPointInitTimes = initiationTime;
            proPointMoveTimes = movementTime;
            AntiPoint = !AntiPoint; //change to other test
            displayNewTestAlert();
            displayToast("First Test Complete. Second Test has started.");
        } else {
            displayToast("All tests complete");
            elapsedEndTime = System.nanoTime();
            completedTests = 0;
            //Exit activity and save data
            String results = generateResults();
            writeToFile(results);
            displayEndTestAlert();
        }
    }

    private void displayEndTestAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Complete");
        builder.setMessage("Thank you for completing the tests, you will now be returned to the main menu.");
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        endTest();
                        dialog.cancel();
                    }
                });
        AlertDialog AntiPointTest = builder.create();
        AntiPointTest.show();
    }

    private void endTest() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void displayNewTestAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ProPoint Test Complete");
        builder.setMessage("ProPoint tests are complete, AntiPoint tests are about to begin.");
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog AntiPointTest = builder.create();
        AntiPointTest.show();
    }

    //Function: arrayAverage
    //Description: Find average value of an array
    private double arrayAverage(double[] array) {
        double avg;
        double sum = 0;
        for (double i : array) {
            sum += i;
        }
        avg = sum / array.length;
        avg = Math.floor(avg * 1000) / 1000;
        return avg;
    }

    //Display a toast
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }

    //Function: generateResults
    //Description: Create CSV string of results
    private String generateResults() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Participant: ," + participant);
        data.append("\nDate and Time: ," + String.valueOf(date));
        data.append("\nElapsed Time (s): ," + String.valueOf(nanotoSeconds(elapsedStartTime, elapsedEndTime)));
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

    //Record a Start Time
    private void startTimer() {
        startTime = System.nanoTime();
    }

    //Record an End Time
    private void endTimer() {
        endTime = System.nanoTime();
    }

    //Return random boolean
    public boolean randomBoolean() {
        return Math.random() < 0.5;
    }

    //Function: nanotoSeconds
    //Description: Converts two nanosecond values and returns the difference in seconds
    private double nanotoSeconds(long startTime, long endTime) {
        double timeResult = (double) (endTime - startTime) / 1000000000;
        timeResult = Math.floor(timeResult * 1000) / 1000;
        return timeResult;
    }
}




