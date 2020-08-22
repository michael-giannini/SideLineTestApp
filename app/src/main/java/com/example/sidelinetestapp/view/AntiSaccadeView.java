package com.example.sidelinetestapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.sidelinetestapp.R;
import com.example.sidelinetestapp.standalone.MainActivity;
import com.example.sidelinetestapp.standalone.saccadeInstructions;
import com.example.sidelinetestapp.viewmodel.SaccadeViewModel;

/*
Class:		AntiSaccadeView
Purpose:    This class contains the UI elements of the anti saccade test.
*/
public class AntiSaccadeView extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private SaccadeViewModel mViewModel;

    public static ImageView[] target = new ImageView[4];
    public static ImageView[] arrow = new ImageView[4];

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_saccade);

        //Retrieve participant ID
        Intent intent = getIntent();
        String participant = intent.getStringExtra(saccadeInstructions.EXTRA_MESSAGE);

        //Use custom viewModel for the AntiSaccade Test
        mViewModel = new ViewModelProvider(this).get(SaccadeViewModel.class);

        //Retrieve number of trials from shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String testStop = sharedPref.getString("saccade_trials", "10");

        //Assign each image view to its array
        for (int i = 0; i < 4; i++) {
            int res = getResources().getIdentifier("target" + i, "id", getPackageName());
            int arrowRes = getResources().getIdentifier("arrow" + i, "id", getPackageName());
            target[i] = findViewById(res);
            arrow[i] = findViewById(arrowRes);
        }
        ImageView mStartCircle = findViewById(R.id.startCircle);

        //Execute this function when the target live data variable is changed in the view model
        final Observer<Integer> resultObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer targ) {
                Log.d(LOG_TAG, "Observed change.");
                if (targ < 4) {
                    target[targ].setImageResource(R.drawable.targetsquare);
                } else if (targ == 5) {
                    for (int i = 0; i < 4; i++) {
                        target[i].setImageResource(R.drawable.emptysquare);
                        arrow[i].setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        //Execute this function when the arrow live data variable is changed in the view model
        final Observer<Integer> arrowObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer targ) {
                Log.d(LOG_TAG, "Observed arrow change.");
                if (targ < 5) {
                    arrow[targ].setVisibility(View.VISIBLE);
                }
            }
        };

        //Execute this function when the message string live data variable is changed in the view model
        final Observer<String> stringObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String msg) {
                Log.d(LOG_TAG, "Observed string change.");
                switch (msg) {
                    case ("New Test"):
                        displayNewTestAlert();
                        break;
                    case ("End Test"):
                        displayEndTestAlert();
                        break;
                    case ("Toast"):
                        displayToast("Please hit the circle");
                        break;
                }
            }
        };

        //State which variables inside the view model should be observed
        mViewModel.getTarget().observe(this, resultObserver);
        mViewModel.getArrow().observe(this, arrowObserver);
        mViewModel.getDisplayString().observe(this, stringObserver);

        //Send data participant and shared preferences to the view model
        mViewModel.setData(participant, Integer.parseInt(testStop));

        //Assign listeners to buttons
        mStartCircle.setOnLongClickListener(startCircleLongClick);
        mStartCircle.setOnTouchListener(actionUp);

    }

    //Execute when long click on start circle happens
    private View.OnLongClickListener startCircleLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.d(LOG_TAG, "Long Click Occurred.");
            mViewModel.startAntiSaccadeTest();
            return true;
        }
    };

    //Execute when finger lifts off middle circle
    private final View.OnTouchListener actionUp = new View.OnTouchListener() {
        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mViewModel.fingerUp();
            }
            return false;
        }
    };

    //Execute view model method when a target is touched
    public void targetTouched(View view) {
        mViewModel.targetTouched(view);
    }

    //Display a pop-up when the anti point test starts
    public void displayNewTestAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
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

    //Display a pop up when the test is over
    private void displayEndTestAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
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

    //Direct user to the main activity
    private void endTest() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Display a toast
    public void displayToast(String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
