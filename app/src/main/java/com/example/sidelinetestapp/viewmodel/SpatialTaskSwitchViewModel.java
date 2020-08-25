package com.example.sidelinetestapp.viewmodel;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sidelinetestapp.model.NumericTaskSwitchModel;
import com.example.sidelinetestapp.model.SpatialTaskSwitchModel;
import com.example.sidelinetestapp.standalone.MainActivity;
import com.example.sidelinetestapp.standalone.Utility;

/*
Class:		SpatialTaskSwitchModel
Author:     Michael Giannini
Purpose:	Act as a bridge between the NumericTaskSwitch Model and NumericTaskSwitchView classes. Responsible
            for conducting the logic for the test and passing data to the model.
*/
public class SpatialTaskSwitchViewModel extends ViewModel {
    private static final String LOG_TAG = SpatialTaskSwitchViewModel.class.getSimpleName();

    //Instantiate the model for the test
    private static SpatialTaskSwitchModel testModel;

    private int switchCount;

    private int conIndex;
    private int inconIndex;
    private int stayIndex;
    private int switchIndex;

    private long startTime;
    private long endTime;

    private boolean activeTop;
    private boolean activeBottom;
    private boolean congruent;

    //Create live data that the view will observe
    private MutableLiveData<String> randSquare;
    private MutableLiveData<String> condition;
    private MutableLiveData<String> display;


    //Create getters for the observer(view) to use
    public MutableLiveData<String> getRandSquare() {
        Log.d(LOG_TAG, "Got pass target.");
        if (randSquare == null) {
            randSquare = new MutableLiveData<String>();
            randSquare.postValue("null");
            Log.d(LOG_TAG, "init");
        }
        return randSquare;
    }

    public MutableLiveData<String> getCondition() {
        Log.d(LOG_TAG, "Got pass target.");
        if (condition == null) {
            condition = new MutableLiveData<String>();
            condition.postValue("null");
            Log.d(LOG_TAG, "init");
        }
        return condition;
    }

    public MutableLiveData<String> getDisplay() {
        Log.d(LOG_TAG, "Got pass target.");
        if (display == null) {
            display = new MutableLiveData<String>();
            display.postValue("null");
            Log.d(LOG_TAG, "init");
        }
        return display;
    }

    public void setData(String participant, int simpleCondLimit, int trialLimit) {
        testModel = new SpatialTaskSwitchModel(participant, simpleCondLimit, trialLimit);
    }

    public void initTest() {
        testModel.elapsedStartTime = Utility.sysTime();
        congruent = true;
    }

    public void displayRandomSquare() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean activeSquare = Math.random() < 0.5;
                if (activeSquare) {
                    randSquare.postValue("TOP");
                    activeTop = true;
                    activeBottom = false;
                } else {
                    randSquare.postValue("BOTTOM");
                    activeBottom = true;
                    activeTop = false;
                }
                startTime = Utility.sysTime();
            }
        }, 100);
    }

    //Determine if the correct button was clicked
    public void leftClick() {
        if (activeBottom) correctAnswer();
        else incorrectAnswer();
    }

    //Determine if the correct button was clicked
    public void rightClick() {
        if (activeTop) correctAnswer();
        else incorrectAnswer();
    }

    //Increment incorrect answers counter
    private void incorrectAnswer() {
        testModel.incorrectAnswers++;
    }

    //Log time and determine if test should be switched
    private void correctAnswer() {
        logTime();
        testModel.correctAnswers++;
        //If we are out of the simple limit increment switch count
        if (testModel.correctAnswers > testModel.simpleLimit) {
            switchCount++;
            //Switch tasks when switchCount ==2
            if (switchCount == 2) taskSwitch();
        }
        //If trial limit is reached, end test
        if (testModel.correctAnswers == testModel.trialLimit) concludeTest();
        randSquare.postValue("NONE");
        displayRandomSquare();
    }

    //Store recorded time in the appropriate array
    private void logTime() {
        long endTime = Utility.sysTime();
        //Store reaction time for simple trials
        if (testModel.correctAnswers < testModel.simpleLimit) {
            testModel.simpleTime[testModel.correctAnswers] = Utility.nanotoSeconds(startTime, endTime);
        }
        //Store reaction time in either Magnitude Array or Parity Array
        if (congruent) {
            Log.d(LOG_TAG, "recording congruent time.");
            testModel.congruentTime[conIndex] = Utility.nanotoSeconds(startTime, endTime);
            conIndex++;
        } else {
            Log.d(LOG_TAG, "recording incongruent time.");
            testModel.incongruentTime[inconIndex] = Utility.nanotoSeconds(startTime, endTime);
            inconIndex++;
        }
        //Store Reaction time in either stay or switch array
        if (switchCount == 1) {
            Log.d(LOG_TAG, "recording stay time.");
            testModel.stayTime[stayIndex] = Utility.nanotoSeconds(startTime, endTime);
            stayIndex++;
        } else if (switchCount == 0 && testModel.correctAnswers > testModel.simpleLimit) {
            Log.d(LOG_TAG, "recording switch time.");
            testModel.switchTime[switchIndex] = Utility.nanotoSeconds(startTime, endTime);
            switchIndex++;
        }
    }

    //Switch task
    private void taskSwitch() {
        Log.d(LOG_TAG, "Switching Task");
        if (congruent) {
            condition.postValue("CONGRUENT");
        } else {
            condition.postValue("NOTCONGRUENT");
        }
        congruent = !congruent;
        switchCount = 0;
    }

    //record end time and trigger method in the model.
    private void concludeTest() {
        testModel.elapsedEndTime = Utility.sysTime();
        testModel.concludeTest();
        display.postValue("End Test");
    }
}
