package com.example.sidelinetestapp.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.sidelinetestapp.BR;
import com.example.sidelinetestapp.R;
import com.example.sidelinetestapp.model.SettingsModel;

/*
Class:		LoginViewModel
Purpose:	Act as a bridge between the SettingsModel and LoginViewModel classes. This class is also
            responsible for determining if the entered pin is the same as the pin stored in the
            Model class. If so, instruct the View to open the settings activity.
*/
public class LoginViewModel extends BaseObservable {

    public static final int OPEN_SETTINGS = 123;

    private SettingsModel pinModel;
    @Bindable
    private int messageID;
    @Bindable
    private String toastMessage = null;

    public LoginViewModel() {
        pinModel = new SettingsModel("");
    }

    /*
    When the Enter button is clicked, determine if the pin matches the stored pin. Changed messageID
    to open the setting activity.
    */
    public void onloginclicked() {
        if (checkLogin()) {
            messageID = OPEN_SETTINGS;
            notifyPropertyChanged(BR.messageID);
        } else {
            setToastMessage("Incorrect Password");
        }
    }

    //Check if pins match
    private boolean checkLogin() {
        return pinModel.getPin().equals("1234");
    }

    //Display a toast
    private void setToastMessage(String message) {
        this.toastMessage = message;
        notifyPropertyChanged(BR.toastMessage);
    }

    public String getToastMessage() {
        return toastMessage;
    }

    public int getMessageID() {
        return messageID;
    }

    @Bindable
    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    @Bindable
    public void setPin(String pin) {
        pinModel.setPin(pin);
        notifyPropertyChanged(BR.pin);
    }

    @Bindable
    public String getPin() {
        return pinModel.getPin();
    }
}
