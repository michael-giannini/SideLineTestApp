package com.example.sidelinetestapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.shape.InterpolateOnScrollPositionChangeHelper;
import com.google.android.material.shape.TriangleEdgeTreatment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NumericTaskSwitch extends AppCompatActivity {

    public static final String EXTRA_MESSAGE =
            "com.example.android.sidelinetestapp.extra.MESSAGE";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static int SIMPLE_COND_LIMIT = 6; //Should be 80 to match paper's method
    private static int TRIAL_LIMIT = 14; //Should be SIMPLE_COND_LIMIT + 160 = 240


    private TextView taskSwitchRandomNumber;
    private Button leftClick;
    private Button rightClick;
    private Button leftClickMinus;
    private Button rightClickPlus;
    private Button startButton;

    private int randNum;
    private int correctAnswers;
    private int incorrectAnswers;
    private int switchCount;

    private int magIndex;
    private int parityIndex;
    private int stayIndex;
    private int switchIndex;

    private double[] magnitudeTime;
    private double[] parityTime;
    private double[] simpleTime;
    private double[] stayTime;
    private double[] switchTime;

    private double meanMagTime;
    private double meanParityTime;
    private double meanSimpleTime;
    private double meanStayTime;
    private double meanSwitchTime;
    private double globalCost;
    private double switchCost;

    private long startTime;
    private long elapsedStartTime;
    private long elapsedEndTime;

    private boolean magnitudeCondition;

    private String strDate;
    private String participant = "Unknown";

    private Date date;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeric_task_switch);
        date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_kmm");
        strDate = formatter.format(date);
        Intent intent = getIntent();
        participant = intent.getStringExtra(TaskSwitchInstructions.EXTRA_MESSAGE);
        taskSwitchRandomNumber = (TextView) findViewById(R.id.taskSwitchRandomNumber);
        startButton = (Button) findViewById(R.id.taskSwitchStartButton);
        leftClick = (Button) findViewById(R.id.taskSwitchLeftButton);
        rightClick = (Button) findViewById(R.id.taskSwitchRightButton);
        leftClickMinus = (Button) findViewById(R.id.taskSwitchLeftButtonMinus);
        rightClickPlus = (Button) findViewById(R.id.taskSwitchRightButtonPlus);
        magnitudeCondition = true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String simpleTrials = sharedPref.getString("simple_trial_number", "80");
        String totalTrials = sharedPref.getString("total_trial_number", "260");
        SIMPLE_COND_LIMIT = Integer.parseInt(simpleTrials);
        TRIAL_LIMIT = Integer.parseInt(totalTrials);
        magnitudeTime = new double[TRIAL_LIMIT];
        parityTime = new double[TRIAL_LIMIT];
        simpleTime = new double[TRIAL_LIMIT];
        stayTime = new double[TRIAL_LIMIT];
        switchTime = new double[TRIAL_LIMIT];
    }

    //Function: startTest
    //Description: Activated when the start button is clicked
    //             Enables buttons and generates a random number
    public void startTest(View view) {
        startButton.setVisibility(View.INVISIBLE);
        taskSwitchRandomNumber.setVisibility(View.VISIBLE);
        generateRandomNumber();
        leftClick.setEnabled(true);
        rightClick.setEnabled(true);
        elapsedStartTime = sysTime();
    }

    @SuppressLint("SetTextI18n")
    //Function: generateRandomNumber
    //Description: After 100ms, a random number is displayed on the textview and the time is recorded
    public void generateRandomNumber() {
        taskSwitchRandomNumber.setText("-");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] randArr = {1, 2, 3, 4, 6, 7, 8, 9}; //don't include 5
                int randIndex = (int) (Math.random() * (randArr.length));
                randNum = randArr[randIndex];
                taskSwitchRandomNumber.setText(Integer.toString(randNum));
                startTime = sysTime();
            }
        }, 100);

    }

    //Function: leftButtonClick
    //Description:
    public void leftButtonClick(View view) {
        if (magnitudeCondition) {
            if (randNum < 5) {
                correctAnswer();
            } else {
                incorrectAnswer();
            }
        } else {
            if (evenNumber(randNum)) {
                correctAnswer();
            } else {
                incorrectAnswer();
            }
        }
    }

    //Function: rightButtonClick
    //Description:
    public void rightButtonClick(View view) {
        if (magnitudeCondition) {
            if (randNum > 5) {
                correctAnswer();
            } else {
                incorrectAnswer();
            }
        } else {
            if (!evenNumber(randNum)) {
                correctAnswer();
            } else {
                incorrectAnswer();
            }
        }

    }

    //Function: correctAnswer
    //Description: Executes commands if the answer is correct
    private void correctAnswer() {
        //displayToast("Correct");
        taskSwitchRandomNumber.setText("--");
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
        generateRandomNumber();
    }

    //Function: incorrectAnswer
    //Description: Executes commands if the answer is incorrect
    private void incorrectAnswer() {
        //displayToast("Wrong");
        incorrectAnswers++;
    }

    private void concludeTest() {
        elapsedEndTime = sysTime();
        calculateMeanTimes();
        calculateCosts();
        String output = generateResults();
        writeToFile(output);
        displayToast("DONE");
        Log.d(LOG_TAG, "Ending Numeric Test, starting Spatial Activity.");
        displayDialogue();
    }

    private void displayDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Complete");
        builder.setMessage("Switching to Spatial Test. \n\nRemember, when the square is on the top " +
                "portion of the screen, press the '7' or '+' button, if it is on the " +
                "bottom portion of the screen, press the '4' or '-' button.");
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchActivities();
                        dialog.cancel();
                    }
                });
        AlertDialog newTestAlert = builder.create();
        newTestAlert.show();
    }

    private void switchActivities() {
        Intent intent = new Intent(this, SpatialTaskSwitch.class);
        intent.putExtra(EXTRA_MESSAGE, participant);
        startActivity(intent);
    }

    private void calculateCosts() {
        globalCost = (meanStayTime - meanSimpleTime) / meanSimpleTime;
        switchCost = (meanSwitchTime - meanSimpleTime) / meanSimpleTime;
    }

    //Function: taskSwitch
    //Description: Switches tasks
    public void taskSwitch() {
        if (magnitudeCondition) {
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
        magnitudeCondition = !magnitudeCondition;
        switchCount = 0;
    }

    //Function: logTime
    //Description: Find the reaction time and store it in the appropriate array
    private void logTime() {
        long endTime = sysTime();
        //Store reaction time for simple trials
        if (correctAnswers < SIMPLE_COND_LIMIT) {
            simpleTime[correctAnswers] = nanotoSeconds(startTime, endTime);
        }
        //Store reaction time in either Magnitude Array or Parity Array
        if (magnitudeCondition) {
            Log.d(LOG_TAG, "recording magnitude time.");
            magnitudeTime[magIndex] = nanotoSeconds(startTime, endTime);
            magIndex++;
        } else {
            Log.d(LOG_TAG, "recording parity time.");
            parityTime[parityIndex] = nanotoSeconds(startTime, endTime);
            parityIndex++;
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

    private String generateResults() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Participant: ," + participant);
        data.append("\nDate and Time: ," + String.valueOf(date));
        data.append("\nElapsed Time (s): ," + String.valueOf(nanotoSeconds(elapsedStartTime, elapsedEndTime)));
        data.append("\n ,Avg Magnitude Time (s),Avg Parity Time (s),Average Simple Time (s)," +
                "Average Stay Time (s),Average Switch Time (s),Global Cost, Switch Cost, Error Rate");
        data.append("\nResults," + String.valueOf(meanMagTime) + ","
                + String.valueOf(meanParityTime) + "," + String.valueOf(meanSimpleTime) + ","
                + String.valueOf(meanStayTime) + "," + String.valueOf(meanSwitchTime) + ","
                + String.valueOf(globalCost) + "," + String.valueOf(switchCost) + "," + String.valueOf(incorrectAnswers / TRIAL_LIMIT));
        data.append("\n\n,Magnitude Times (s),Parity Times (s),Simple Times(s),Stay Times(s),Switch Times(s)");
        for (int i = 0; i < TRIAL_LIMIT; i++) {
            data.append("\n," + String.valueOf(magnitudeTime[i]) + "," + String.valueOf(parityTime[i]) +
                    "," + String.valueOf(simpleTime[i]) + "," + String.valueOf(stayTime[i]) +
                    "," + String.valueOf(switchTime[i]));
        }
        return data.toString();
    }

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

    private void calculateMeanTimes() {
        meanMagTime = arrayAverage(magnitudeTime);
        meanParityTime = arrayAverage(parityTime);
        meanSimpleTime = arrayAverage(simpleTime);
        meanStayTime = arrayAverage(stayTime);
        meanSwitchTime = arrayAverage(switchTime);
    }

    //Display a toast
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
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

    //Return system time
    private long sysTime() {
        Log.d(LOG_TAG, "Collected system time.");
        return System.nanoTime();
    }

    private double nanotoSeconds(long startTime, long endTime) {
        double timeResult = (double) (endTime - startTime) / 1000000000;
        timeResult = Math.floor(timeResult * 1000) / 1000;
        return timeResult;
    }

    //Function: evenNumber
    //Description: Returns true if passed integer is even, false otherwise
    private boolean evenNumber(int num) {
        return num % 2 == 0;
    }


}
