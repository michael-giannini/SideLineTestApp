package com.example.sidelinetestapp.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.sidelinetestapp.R;
import com.example.sidelinetestapp.standalone.TaskSwitchInstructions;
import com.example.sidelinetestapp.viewmodel.NumericTaskSwitchViewModel;

/*
Class:		NumericTaskSwitchView
Author:     Michael Giannini
Purpose:	Contains all the UI components and UI interactions of this activity.
*/
public class NumericTaskSwitchView extends AppCompatActivity {

    private static final String LOG_TAG = NumericTaskSwitchView.class.getSimpleName();
    private static final String EXTRA_MESSAGE = "com.example.android.sidelinetestapp.extra.MESSAGE";

    private NumericTaskSwitchViewModel mViewModel;

    private TextView taskSwitchRandomNumber;
    private Button leftClick;
    private Button rightClick;
    private Button leftClickMinus;
    private Button rightClickPlus;
    private Button startButton;
    private String participant;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeric_task_switch);

        //Retrieve Partipcipant from Main2Activity
        Intent intent = getIntent();
        participant = intent.getStringExtra(TaskSwitchInstructions.EXTRA_MESSAGE);

        //Use custom viewModel for the AntiSaccade Test
        mViewModel = new ViewModelProvider(this).get(NumericTaskSwitchViewModel.class);

        taskSwitchRandomNumber = (TextView) findViewById(R.id.taskSwitchRandomNumber);
        startButton = (Button) findViewById(R.id.taskSwitchStartButton);
        leftClick = (Button) findViewById(R.id.taskSwitchLeftButton);
        rightClick = (Button) findViewById(R.id.taskSwitchRightButton);
        leftClickMinus = (Button) findViewById(R.id.taskSwitchLeftButtonMinus);
        rightClickPlus = (Button) findViewById(R.id.taskSwitchRightButtonPlus);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String simpleTrials = sharedPref.getString("simple_trial_number", "20");
        String totalTrials = sharedPref.getString("total_trial_number", "60");
        //Should be 80 to match paper's method
        int SIMPLE_COND_LIMIT = Integer.parseInt(simpleTrials);
        //Should be SIMPLE_COND_LIMIT + 160 = 240
        int TRIAL_LIMIT = Integer.parseInt(totalTrials);

        //Execute this function when the arrow live data variable is changed in the view model
        final Observer<Integer> numObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer randNum) {
                Log.d(LOG_TAG, "Observed num change.");
                if (randNum == 0) {
                    taskSwitchRandomNumber.setText("-");
                } else {
                    taskSwitchRandomNumber.setText(Integer.toString(randNum));
                }
            }
        };

        final Observer<String> condObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String condition) {
                Log.d(LOG_TAG, "Observed condition change.");
                if (condition.equals("parity")) {
                    leftClick.setEnabled(false);
                    rightClick.setEnabled(false);
                    leftClickMinus.setEnabled(true);
                    rightClickPlus.setEnabled(true);
                } else if (condition.equals("magnitude")) {
                    leftClick.setEnabled(true);
                    rightClick.setEnabled(true);
                    leftClickMinus.setEnabled(false);
                    rightClickPlus.setEnabled(false);
                }
            }
        };

        final Observer<String> dispObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String msg) {
                Log.d(LOG_TAG, "Observed condition change.");
                if (msg.equals("End Test")) {
                    endTestDialogue();
                }
            }
        };

        //State which variables inside the view model should be observed
        mViewModel.getRandNum().observe(this, numObserver);
        mViewModel.getCondition().observe(this, condObserver);
        mViewModel.getDisplay().observe(this, dispObserver);

        //Send data participant and shared preferences to the view model
        mViewModel.setData(participant, SIMPLE_COND_LIMIT, TRIAL_LIMIT);
    }

    //Display a pop-up when the test is over, then execute switchActivities()
    private void endTestDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Complete");
        builder.setCancelable(false);
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

    //Function: startTest
    //Description: Activated when the start button is clicked
    //             Enables buttons and generates a random number
    public void startTest(View view) {
        startButton.setVisibility(View.INVISIBLE);
        taskSwitchRandomNumber.setVisibility(View.VISIBLE);
        leftClick.setEnabled(true);
        rightClick.setEnabled(true);
        mViewModel.generateRandomNumber();
        mViewModel.startElapsedTime();
    }

    //Function: SwitchActivities
    //Description: When Numeric task switch is complete, start SpatialTaskSwitch activity
    private void switchActivities() {
        Intent intent = new Intent(this, SpatialTaskSwitchView.class);
        intent.putExtra(EXTRA_MESSAGE, participant);
        startActivity(intent);
    }

    //Function: leftButtonClick
    //Description: Start ViewModel logic
    public void leftButtonClick(View view) {
        mViewModel.leftButtonClick();
    }

    //Function: rightButtonClick
    //Description: Start ViewModel logic
    public void rightButtonClick(View view) {
        mViewModel.rightButtonClick();
    }

}
