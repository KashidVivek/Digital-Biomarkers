package com.example.digitalbiomarkers;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Screen;
import com.aware.providers.Screen_Provider;

public class MainActivity extends AppCompatActivity {
    private int screenTime = 0;
    private int numUnlocks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Aware.startAWARE(this);
        Aware.setSetting(this, Aware_Preferences.STATUS_SCREEN,true);
//        TextView sTime = (TextView)findViewById(R.id.sampleText);
//        sTime.setText(Integer.toString(screenTime) + "mins");

        TextView numUnlockTV = (TextView)findViewById(R.id.unlocksTextView);
        Screen.setSensorObserver(new Screen.AWARESensorObserver() {
            @Override
            public void onScreenOn() {

            }

            @Override
            public void onScreenOff() {

            }

            @Override
            public void onScreenLocked() {

            }

            @SuppressLint("WrongConstant")
            @Override
            public void onScreenUnlocked() {
                numUnlocks++;
                numUnlockTV.setText(Integer.toString(numUnlocks));
            }
        });

    }

}

