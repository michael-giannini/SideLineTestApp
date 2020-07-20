package com.example.sidelinetestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TaskSwitchInstructions extends AppCompatActivity {
    private String participant;
    public static final String EXTRA_MESSAGE =
            "com.example.android.sidelinetestapp.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_switch_instructions);
        Intent intent = getIntent();
        participant = intent.getStringExtra(Main2Activity.EXTRA_MESSAGE);
    }

    public void launchTaskSwitchingTest(View view) {
        Intent intent = new Intent(this, NumericTaskSwitch.class);
        intent.putExtra(EXTRA_MESSAGE, participant);
        startActivity(intent);
    }
}
