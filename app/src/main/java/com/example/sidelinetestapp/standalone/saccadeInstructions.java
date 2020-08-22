package com.example.sidelinetestapp.standalone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sidelinetestapp.R;
import com.example.sidelinetestapp.view.AntiSaccadeView;

/*
Class:		saccadeInstructions
Purpose:    This class contains displays the instructions for the anti-saccade test.
*/
public class saccadeInstructions extends AppCompatActivity {
    private String participant;
    public static final String EXTRA_MESSAGE =
            "com.example.android.sidelinetestapp.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saccade_instructions);
        Intent intent = getIntent();
        participant = intent.getStringExtra(Main2Activity.EXTRA_MESSAGE);
    }

    //Launch the Anti-Saccade Test
    public void launchAntiSaccadeTest(View view) {
        Intent intent = new Intent(this, AntiSaccadeView.class);
        intent.putExtra(EXTRA_MESSAGE, participant);
        startActivity(intent);
    }
}
