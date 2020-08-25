package com.example.sidelinetestapp.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.sidelinetestapp.R;
import com.example.sidelinetestapp.standalone.MainActivity;
import com.example.sidelinetestapp.standalone.saccadeInstructions;
import com.example.sidelinetestapp.viewmodel.NumericTaskSwitchViewModel;
import com.example.sidelinetestapp.viewmodel.SpatialTaskSwitchViewModel;

/*
Class:		SpatialTaskSwitchView
Author:     Michael Giannini
Purpose:	Contains all the UI components of the activity and UI interactions
*/
public class SpatialTaskSwitchView extends AppCompatActivity {
    private static final String LOG_TAG = SpatialTaskSwitchView.class.getSimpleName();

    private Button leftClick;
    private Button rightClick;
    private Button leftClickMinus;
    private Button rightClickPlus;
    private Button startButton;
    private ImageView topSquare;
    private ImageView bottomSquare;
    private ImageView divider;

    private SpatialTaskSwitchViewModel mViewModel;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Starting Spatial Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spatial_task_switch);

        startButton = (Button) findViewById(R.id.taskSwitchStartButton);
        leftClick = (Button) findViewById(R.id.taskSwitchLeftButton);
        rightClick = (Button) findViewById(R.id.taskSwitchRightButton);
        leftClickMinus = (Button) findViewById(R.id.taskSwitchLeftButtonMinus);
        rightClickPlus = (Button) findViewById(R.id.taskSwitchRightButtonPlus);
        topSquare = (ImageView) findViewById(R.id.topSquare);
        bottomSquare = (ImageView) findViewById(R.id.bottomSquare);
        divider = (ImageView) findViewById(R.id.divider);

        Intent intent = getIntent();
        String participant = intent.getStringExtra(saccadeInstructions.EXTRA_MESSAGE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String simpleTrials = sharedPref.getString("simple_trial_number", "20");
        String totalTrials = sharedPref.getString("total_trial_number", "60");
        int SIMPLE_COND_LIMIT = Integer.parseInt(simpleTrials);
        int TRIAL_LIMIT = Integer.parseInt(totalTrials);

        //Use custom viewModel for the AntiSaccade Test
        mViewModel = new ViewModelProvider(this).get(SpatialTaskSwitchViewModel.class);
        mViewModel.setData(participant, SIMPLE_COND_LIMIT, TRIAL_LIMIT);

        //Define actions to be taken when observed variables change
        final Observer<String> squareObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String square) {
                Log.d(LOG_TAG, "Observed square change.");
                if (square.equals("TOP")) {
                    topSquare.setVisibility(View.VISIBLE);
                } else if (square.equals("BOTTOM")) {
                    bottomSquare.setVisibility(View.VISIBLE);
                } else if (square.equals("NONE")) {
                    bottomSquare.setVisibility(View.INVISIBLE);
                    topSquare.setVisibility(View.INVISIBLE);
                }
            }
        };

        final Observer<String> condObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String cond) {
                Log.d(LOG_TAG, "Observed square change.");
                if (cond.equals("CONGRUENT")) {
                    leftClick.setEnabled(false);
                    rightClick.setEnabled(false);
                    leftClickMinus.setEnabled(true);
                    rightClickPlus.setEnabled(true);
                } else if (cond.equals("NOTCONGRUENT")) {
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
        mViewModel.getRandSquare().observe(this, squareObserver);
        mViewModel.getCondition().observe(this, condObserver);
        mViewModel.getDisplay().observe(this, dispObserver);
    }

    //Commands to execute when test is started
    public void startSpatialTest(View view) {
        startButton.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.VISIBLE);
        leftClick.setEnabled(true);
        rightClick.setEnabled(true);
        mViewModel.initTest();
        mViewModel.displayRandomSquare();
    }

    //Function: leftButtonClick
    //Description: Start a viewModel function when left button is clicked
    public void leftButtonClick(View view) {
        mViewModel.leftClick();
    }

    //Function: rightButtonClick
    //Description: Start a viewModel function when right button is clicked
    public void rightButtonClick(View view) {
        mViewModel.rightClick();
    }

    //Display a pop-up when the test has ended
    private void endTestDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Complete");
        builder.setCancelable(false);
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

    //open the main activity
    private void switchActivities() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
