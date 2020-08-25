package com.example.sidelinetestapp.viewmodel;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sidelinetestapp.model.NumericTaskSwitchModel;
import com.example.sidelinetestapp.standalone.Utility;

/*
Class:		TaskSwitchViewModel
Author:     Michael Giannini
Purpose:	Act as a bridge between the AntiSaccadeModel and AntiSaccadeView classes. Responsible
            for conducting the logic for the test and passing data to the model.
*/
public class NumericTaskSwitchViewModel extends ViewModel {

    private static final String LOG_TAG = SaccadeViewModel.class.getSimpleName();

    //Instantiate the model for the test
    private static NumericTaskSwitchModel testModel;

    private boolean magnitudeCondition;
    private int switchCount;
    private int magIndex;
    private int parityIndex;
    private int stayIndex;
    private int switchIndex;
    private int randNumber;
    private static long startTime;

    //Create live data that the view will observe
    private MutableLiveData<Integer> randNum;
    private MutableLiveData<String> condition;
    private MutableLiveData<String> display;

    //Create getters for the observer(view) to use
    public MutableLiveData<Integer> getRandNum() {
        Log.d(LOG_TAG, "Got pass target.");
        if (randNum == null) {
            randNum = new MutableLiveData<Integer>();
            randNum.postValue(0);
            Log.d(LOG_TAG, "init");
        }
        return randNum;
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

    //Send Participant and shared preferences to the model
    public void setData(String participant, int simpleCondLimit, int trialLimit) {
        testModel = new NumericTaskSwitchModel(participant, simpleCondLimit, trialLimit);
    }

    //Record starting time
    public void startElapsedTime() {
        testModel.elapsedStartTime = Utility.sysTime();
        magnitudeCondition = true;
    }

    @SuppressLint("SetTextI18n")
    //Function: generateRandomNumber
    //Description: After 100ms, a random number is displayed on the textview and the time is recorded
    public void generateRandomNumber() {
        randNum.postValue(0);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] randArr = {1, 2, 3, 4, 6, 7, 8, 9}; //don't include 5
                int randIndex = (int) (Math.random() * (randArr.length));
                randNum.postValue(randArr[randIndex]);
                randNumber = randArr[randIndex];
                startTime = Utility.sysTime();
            }
        }, 100);
    }

    //Determine if the correct button was clicked
    public void leftButtonClick() {
        if (magnitudeCondition) {
            if (randNumber < 5) {
                correctAnswer();
            } else {
                incorrectAnswer();
            }
        } else {
            if (evenNumber(randNumber)) {
                correctAnswer();
            } else {
                incorrectAnswer();
            }
        }
    }

    //Determine if the correct button was clicked
    public void rightButtonClick() {
        if (magnitudeCondition) {
            if (randNumber > 5) {
                correctAnswer();
            } else {
                incorrectAnswer();
            }
        } else {
            if (!evenNumber(randNumber)) {
                correctAnswer();
            } else {
                incorrectAnswer();
            }
        }

    }

    //Function: incorrectAnswer
    //Description: Executes commands if the answer is incorrect
    private void incorrectAnswer() {
        testModel.incorrectAnswers++;
    }

    //Function: correctAnswer
    //Description: Executes commands if the answer is correct
    private void correctAnswer() {
        //displayToast("Correct");
        randNum.postValue(0);
        logTime();
        testModel.correctAnswers++;
        //If we are out of the simple limit, increment switch count
        if (testModel.correctAnswers > testModel.simpleLimit) {
            switchCount++;
            //Switch tasks when switchCount ==2
            if (switchCount == 2) taskSwitch();
        }
        //If trial limit is reached, end test
        if (testModel.correctAnswers == testModel.trialLimit) concludeTest();
        generateRandomNumber();
    }

    //Log the time in the appropriate array
    private void logTime() {
        long endTime = Utility.sysTime();
        //Store reaction time for simple trials
        if (testModel.correctAnswers < testModel.simpleLimit) {
            testModel.simpleTime[testModel.correctAnswers] = Utility.nanotoSeconds(startTime, endTime);
        }
        //Store reaction time in either Magnitude Array or Parity Array
        if (magnitudeCondition) {
            Log.d(LOG_TAG, "recording magnitude time.");
            testModel.magnitudeTime[magIndex] = Utility.nanotoSeconds(startTime, endTime);
            magIndex++;
        } else {
            Log.d(LOG_TAG, "recording parity time.");
            testModel.parityTime[parityIndex] = Utility.nanotoSeconds(startTime, endTime);
            parityIndex++;
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

    //Function: taskSwitch
    //Description: Switches tasks
    private void taskSwitch() {
        if (magnitudeCondition) {
            condition.postValue("parity");
        } else {
            condition.postValue("magnitude");
        }
        magnitudeCondition = !magnitudeCondition;
        switchCount = 0;
    }

    //trigger a method in the model when the test is complete
    private void concludeTest() {
        testModel.elapsedEndTime = Utility.sysTime();
        testModel.concludeTest();
        display.postValue("End Test");
    }


    //Function: evenNumber
    //Description: Returns true if passed integer is even, false otherwise
    private boolean evenNumber(int num) {
        return num % 2 == 0;
    }


}
