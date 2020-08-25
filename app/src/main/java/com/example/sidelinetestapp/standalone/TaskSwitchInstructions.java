package com.example.sidelinetestapp.standalone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

//import com.example.sidelinetestapp.NumericTaskSwitch;
import com.example.sidelinetestapp.R;
import com.example.sidelinetestapp.view.NumericTaskSwitchView;

/*
Class:		TaskSwitchInstructions
Author:     Michael Giannini
Purpose:    This class contains displays the instructions for the task switch test.
*/
public class TaskSwitchInstructions extends AppCompatActivity {
    private static final String LOG_TAG = TaskSwitchInstructions.class.getSimpleName();
    private String participant;
    public static final String EXTRA_MESSAGE =
            "com.example.android.sidelinetestapp.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Starting Task Switch Instructions");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_switch_instructions);
        Intent intent = getIntent();
        participant = intent.getStringExtra(Main2Activity.EXTRA_MESSAGE);
    }

    //Launch task switch test
    public void launchTaskSwitchingTest(View view) {
        Intent intent = new Intent(this, NumericTaskSwitchView.class);
        intent.putExtra(EXTRA_MESSAGE, participant);
        startActivity(intent);
    }
}
