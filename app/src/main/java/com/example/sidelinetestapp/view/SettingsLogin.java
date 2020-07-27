package com.example.sidelinetestapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sidelinetestapp.R;
import com.example.sidelinetestapp.testSettings;
import com.example.sidelinetestapp.databinding.ActivitySettingsLoginBinding;
import com.example.sidelinetestapp.viewmodel.LoginViewModel;


/*
Class:		SettingsLogin
Purpose:	Display the settings login layout
*/
public class SettingsLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_login);
        ActivitySettingsLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_login);
        binding.setLoginViewModel(new LoginViewModel());
        binding.executePendingBindings();
    }

    @BindingAdapter({"showMessage"})
    public static void openSettings(View view, int messageID) {
        if (messageID == LoginViewModel.OPEN_SETTINGS) {
            view.getContext().startActivity(new Intent(view.getContext(), testSettings.class));
        }
    }

    @BindingAdapter({"toastMessage"})
    public static void runMe(View view, String message) {
        if (message != null)
            Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
