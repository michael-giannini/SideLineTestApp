package com.example.sidelinetestapp.model;

import com.example.sidelinetestapp.R;


/*
Class:		SettingsModel
Purpose:	Store the pin number that is needed to access the app's settings.
*/
public class SettingsModel {

    private String pin;

    public SettingsModel(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String newPin) {
        this.pin = newPin;
    }
}
