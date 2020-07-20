package com.example.sidelinetestapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Main2Activity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE =
            "com.example.android.sidelinetestapp.extra.MESSAGE";
    private EditText mIDEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "-------");
        Log.d(LOG_TAG, "Participant Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mIDEditText = findViewById(R.id.IDeditText);
    }

    //Launch Anti-Saccade Test
    public void launchAntiSaccadeTest(View view) {
        String participantID = mIDEditText.getText().toString();
        if (participantID.equals("")) {
            displayNameAlert();
            return;
        }
        Intent intent = new Intent(this, saccadeInstructions.class);
        intent.putExtra(EXTRA_MESSAGE, participantID);
        startActivity(intent);
    }

    //Launch Task Switching Test
    public void launchTaskSwitchTest(View view) {
        String participantID = mIDEditText.getText().toString();
        if (participantID.equals("")) {
            displayNameAlert();
            return;
        }
        Intent intent = new Intent(this, TaskSwitchInstructions.class);
        intent.putExtra(EXTRA_MESSAGE, participantID);
        startActivity(intent);
    }

    private void displayNameAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Name Entered");
        builder.setMessage("Please enter your name to begin the test.");
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog endOfTestAlert = builder.create();
        endOfTestAlert.show();
    }

}
