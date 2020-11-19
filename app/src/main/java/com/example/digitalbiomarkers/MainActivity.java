package com.example.digitalbiomarkers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Communication;
import com.aware.plugin.device_usage.Provider;
import com.aware.providers.Communication_Provider;
import com.aware.providers.Keyboard_Provider;
import com.aware.providers.Screen_Provider;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;

public class MainActivity extends AppCompatActivity {
    private long screenTime = 0;
    private int numUnlocks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new RetrieveFeedTask(this).execute();
        IamAuthenticator authenticator = new IamAuthenticator("Ufr3FL9wHAotv_f7QItN7Q_EKvUHkpK-B88J5USZWloR");
        ToneAnalyzer toneAnalyzer = new ToneAnalyzer("8.6.3",authenticator);
        toneAnalyzer.setServiceUrl("https://api.us-south.tone-analyzer.watson.cloud.ibm.com/instances/94baf436-8819-49d9-af67-cf6c3fc3345c");

        Button analyzeButton = (Button)findViewById(R.id.button);

        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // More code here
//                Log.d("Button click","******************");
//                EditText userInput = (EditText)findViewById(R.id.user_input);
//                Toast toast = Toast.makeText(getApplicationContext(),"msg",Toast.LENGTH_LONG);
//                toast.show();
                RetrieveFeedTask emotion = new RetrieveFeedTask(getApplicationContext());
                emotion.execute();
            }
        });
        Aware.startAWARE(this);
        Aware.setSetting(this, Aware_Preferences.STATUS_SCREEN,true);
        Aware.setSetting(this, Aware_Preferences.STATUS_KEYBOARD,true);
        Aware.setSetting(this,"status_plugin_device_usage",true);
        Aware.setSetting(this,Aware_Preferences.STATUS_COMMUNICATION_EVENTS,true);

        String[] tableValuesScreen =
                {
                        Screen_Provider.Screen_Data._ID,
                        Screen_Provider.Screen_Data.TIMESTAMP,
                        Screen_Provider.Screen_Data.DEVICE_ID,
                        Screen_Provider.Screen_Data.SCREEN_STATUS
                };
        Cursor cursor = getContentResolver().query(Screen_Provider.Screen_Data.CONTENT_URI,tableValuesScreen, null,null,null);

        String[] tableValuesKB =
                {
                        Keyboard_Provider.Keyboard_Data._ID,
                        Keyboard_Provider.Keyboard_Data.DEVICE_ID,
                        Keyboard_Provider.Keyboard_Data.BEFORE_TEXT,
                        Keyboard_Provider.Keyboard_Data.PACKAGE_NAME,
                        Keyboard_Provider.Keyboard_Data.CURRENT_TEXT
                };
        Cursor keyboardCursor = getContentResolver().query(Keyboard_Provider.Keyboard_Data.CONTENT_URI,tableValuesKB, null,null,null);

        String[] tableValuesDU =
                {
                        Provider.DeviceUsage_Data._ID,
                        Provider.DeviceUsage_Data.DEVICE_ID,
                        Provider.DeviceUsage_Data.ELAPSED_DEVICE_ON,
                        Provider.DeviceUsage_Data.ELAPSED_DEVICE_OFF
                };
        Cursor deviceUsageCursor = getContentResolver().query(Provider.DeviceUsage_Data.CONTENT_URI,tableValuesDU, null,null,null);

        String[] tableValuesComms =
                {
                        Communication_Provider.Calls_Data._ID,
                        Communication_Provider.Calls_Data.DEVICE_ID,
                        Communication_Provider.Calls_Data.DURATION,
                        Communication_Provider.Calls_Data.TIMESTAMP,
                        Communication_Provider.Calls_Data.TYPE,
//                        Communication_Provider.Messages_Data.TYPE,
                        Communication_Provider.Messages_Data.TIMESTAMP
                };
        Cursor CommsCursor = getContentResolver().query(Communication_Provider.Calls_Data.CONTENT_URI,tableValuesComms,null,null,null);
        Communication.setSensorObserver(new Communication.AWARESensorObserver() {
            @Override
            public void onCall(ContentValues data) {
                if(CommsCursor != null){
                    CommsCursor.moveToFirst();
                    for (int i = 0; i < CommsCursor.getCount(); i++) {
                        Log.d("Message Data", CommsCursor.getString(CommsCursor.getColumnIndexOrThrow(Communication_Provider.Calls_Data.TIMESTAMP)));
                    }
                }
            }

            @Override
            public void onMessage(ContentValues data) {

            }

            @Override
            public void onRinging(String number) {

            }

            @Override
            public void onBusy(String number) {

            }

            @Override
            public void onFree(String number) {

            }
        });

    }

}

