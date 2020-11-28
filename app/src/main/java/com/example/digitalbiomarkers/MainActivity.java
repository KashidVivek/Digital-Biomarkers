package com.example.digitalbiomarkers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Communication;
import com.aware.Screen;
import com.aware.plugin.device_usage.Provider;
import com.aware.providers.Applications_Provider;
import com.aware.providers.Communication_Provider;
import com.aware.providers.Keyboard_Provider;
import com.aware.providers.Screen_Provider;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private long screenTime = 0;
    private int numUnlocks = 0;
    public  String tone;
    public String textToAnalyse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IamAuthenticator authenticator = new IamAuthenticator("Ufr3FL9wHAotv_f7QItN7Q_EKvUHkpK-B88J5USZWloR");
        ToneAnalyzer toneAnalyzer = new ToneAnalyzer("8.6.3",authenticator);
        toneAnalyzer.setServiceUrl("https://api.us-south.tone-analyzer.watson.cloud.ibm.com/instances/94baf436-8819-49d9-af67-cf6c3fc3345c");
        String selection = Keyboard_Provider.Keyboard_Data._ID + " IN (SELECT "
                + Keyboard_Provider.Keyboard_Data._ID +"- 1"+ " FROM "
                + "keyboard" + " WHERE "
                + Keyboard_Provider.Keyboard_Data.BEFORE_TEXT + "=?)";
        String sel =  Keyboard_Provider.Keyboard_Data.BEFORE_TEXT + "=?";
        String[] selectionargs = new String[]{""};
        Button analyzeButton = (Button)findViewById(R.id.button);
        TextView sTime = (TextView) findViewById(R.id.sampleText);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor keyboardCursor = getContentResolver().query(Keyboard_Provider.Keyboard_Data.CONTENT_URI,null, selection,selectionargs,null);
                List<String> text = new ArrayList<String>();

                if (keyboardCursor!=null){
                    keyboardCursor.moveToFirst();
                    do{
                        text.add(keyboardCursor.getString(keyboardCursor.getColumnIndexOrThrow(Keyboard_Provider.Keyboard_Data.CURRENT_TEXT)));
                    }while(keyboardCursor.moveToNext());
                    textToAnalyse = text.toString().replace("[","").replace("]","");
//                    Toast.makeText(getApplicationContext(), "MainActivity"+textToAnalyse, Toast.LENGTH_SHORT).show();
                    RetrieveFeedTask emotion = new RetrieveFeedTask(getApplicationContext(),textToAnalyse);
                    try {
                        tone = emotion.execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sTime.setText(tone);
                    getApplicationContext().getContentResolver().delete(Keyboard_Provider.Keyboard_Data.CONTENT_URI,null,null);
                }
            }
        });
        Aware.startAWARE(this);
//        Aware.joinStudy(getApplicationContext(),"http://167.71.59.111:8080/index.php/1/4lph4num3ric");
        sTime.setText(Boolean.toString(Aware.isStudy(getApplicationContext())));
//        Aware.joinStudy(this,"http://10.20.188.9:8080/index.php/1/digitalHealthF2020");
        Aware.setSetting(this, Aware_Preferences.STATUS_SCREEN,true);
        Aware.setSetting(this, Aware_Preferences.STATUS_KEYBOARD,true);
        Aware.setSetting(this,"status_plugin_device_usage",true);
        Aware.startPlugin(this,"status_device_usage");
        Aware.setSetting(this,Aware_Preferences.STATUS_COMMUNICATION_EVENTS,true);
        Aware.setSetting(this,Aware_Preferences.STATUS_CALLS,true);
        Aware.setSetting(this,Aware_Preferences.STATUS_MESSAGES,true);
        Aware.setSetting(this,Aware_Preferences.STATUS_APPLICATIONS,true);

        Applications.isAccessibilityServiceActive(getApplicationContext());

        Cursor cursor = getContentResolver().query(Screen_Provider.Screen_Data.CONTENT_URI,null, null,null,null);
        Cursor deviceUsageCursor = getContentResolver().query(Provider.DeviceUsage_Data.CONTENT_URI,null, null,null,null);
        Cursor CommsCursor = getContentResolver().query(Communication_Provider.Calls_Data.CONTENT_URI,null,null,null,null);
        Cursor AppsCursor = getContentResolver().query(Applications_Provider.Applications_Foreground.CONTENT_URI,null,null,null,null);
//        Cursor keyboardCursor = getContentResolver().query(Keyboard_Provider.Keyboard_Data.CONTENT_URI,null, selection,selectionargs,null);
//        List<String> text = new ArrayList<String>();
//
//
//        if (keyboardCursor!=null){
//            keyboardCursor.moveToFirst();
//            do{
//                text.add(keyboardCursor.getString(keyboardCursor.getColumnIndexOrThrow(Keyboard_Provider.Keyboard_Data.CURRENT_TEXT)));
//            }while(keyboardCursor.moveToNext());
//            setAnalysisText(text.toString().replace("[","").replace("]",""));
////            sTime.setText(text.toString().replace("[","").replace("]",""));
////            sTime.setText(tone);
//        }
//        else{
//            sTime.setText("NOT WORKING");
//        }

    }

    private void setAnalysisText(String replace) {
        textToAnalyse = replace;
    }

//    public static void setTone(String setterTone){
//        tone = setterTone;
//    }

}

