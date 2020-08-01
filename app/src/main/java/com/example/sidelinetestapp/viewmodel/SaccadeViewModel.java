package com.example.sidelinetestapp.viewmodel;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sidelinetestapp.model.AntiSaccadeModel;


public class SaccadeViewModel extends ViewModel {

    private static final String LOG_TAG = SaccadeViewModel.class.getSimpleName();

    private MutableLiveData<Integer> passingTarget;
    private MutableLiveData<Integer> liveArrowTarget;
    private MutableLiveData<String> displayString;

    public MutableLiveData<Integer> getTarget() {
        Log.d(LOG_TAG, "Got pass target.");
        if (passingTarget == null) {
            passingTarget = new MutableLiveData<Integer>();
            passingTarget.postValue(5);
            Log.d(LOG_TAG, "init");
        }
        return passingTarget;
    }

    public MutableLiveData<Integer> getArrow() {
        Log.d(LOG_TAG, "Got arrow target.");
        if (liveArrowTarget == null) {
            liveArrowTarget = new MutableLiveData<Integer>();
            liveArrowTarget.postValue(5);
            Log.d(LOG_TAG, "init");
        }
        return liveArrowTarget;
    }

    public MutableLiveData<String> getDisplayString() {
        Log.d(LOG_TAG, "Got string target.");
        if (displayString == null) {
            displayString = new MutableLiveData<String>();
            displayString.postValue("null");
            Log.d(LOG_TAG, "init");
        }
        return displayString;
    }

    private static long startTime;
    private static long endTime;
    private int correctTarget; //indicates the correct target
    private static boolean activeTarget; //indicates if there is an active target waiting to be pressed
    private boolean AntiPoint; //Indicates if it is a Pro-Point test or Anti-Point

    private static AntiSaccadeModel testModel;

    public SaccadeViewModel() {
        testModel = new AntiSaccadeModel(System.nanoTime());
    }

    public void setData(String participantID, int testLimit) {
        testModel.participant = participantID;
        testModel.TEST_STOP = testLimit;
    }

    public void onClick() {
        startAntiSaccadeTest();
    }

    //Execute when long click on start circle happens
    private void startAntiSaccadeTest() {
        Log.d(LOG_TAG, "Starting trial");
        if (!activeTarget) {
            //choose a random square to become the target
            //indicates which arrow should be visible
            //indicates which square should be visible
            int newTarget;
            int arrowTarget = newTarget = correctTarget = (int) (Math.random() * ((4 - 1) + 1) + 0);
            passingTarget.postValue(newTarget);
            Log.d(LOG_TAG, "passingTargetSet: " + newTarget);
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
            liveArrowTarget.postValue(arrowTarget);
            startTime = System.nanoTime();
            activeTarget = true;
        } else {
            displayString.postValue("Toast");
        }
    }

    public void fingerUp() {
        if (activeTarget) {
            Log.d(LOG_TAG, "Action up");
            //Get timestamp for initiation time
            endTime = System.nanoTime();
            testModel.initiationTime[testModel.correctAnswers] = nanotoSeconds(startTime, endTime);
            //Start new time for movement time
            startTime = System.nanoTime();
        }
    }

    //Function: targetTouched
    //Description: When a square is touched, determine if it is the correct square. If it is the
    //correct square, record times
    @SuppressLint("SetTextI18n")

    public void targetTouched(View view) {
        //Determine if there is an active target
        if (activeTarget) {
            //Read which square was touched
            String clickedTarget = view.getTag().toString();

            //Determine if correct square was touched
            if (clickedTarget.equals(Integer.toString(correctTarget))) {
                Log.d(LOG_TAG, "Correct square.");
                endTime = System.nanoTime();
                testModel.movementTime[testModel.correctAnswers] = nanotoSeconds(startTime, endTime);
                resetTargets();
                testModel.correctAnswers++;
                //Start 2nd half of Anti-Saccade test
                if (testModel.correctAnswers == testModel.TEST_STOP) concludeTest();

            } else {
                Log.d(LOG_TAG, "Incorrect square");
                resetTargets();
                testModel.incorrectAnswers[testModel.completedTests]++;
            }
        }
    }

    //Function: resetTargets
    //Description: Remove active targets from the screen
    private void resetTargets() {
        passingTarget.setValue(5);
        activeTarget = false;
    }


    @SuppressLint("SetTextI18n")
    private void concludeTest() {
        testModel.concludeTest();
        Log.d(LOG_TAG, "Tests Completed: " + testModel.completedTests);
        if (testModel.completedTests == 1) {
            AntiPoint = !AntiPoint; //change to other test
            displayString.postValue("New Test");
        } else {
            displayString.postValue("End Test");
        }
    }

    //Function: nanotoSeconds
    //Description: Converts two nanosecond values and returns the difference in seconds
    private static double nanotoSeconds(long startTime, long endTime) {
        double timeResult = (double) (endTime - startTime) / 1000000000;
        timeResult = Math.floor(timeResult * 1000) / 1000;
        return timeResult;
    }

}
