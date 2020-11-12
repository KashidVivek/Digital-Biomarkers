package com.example.digitalbiomarkers;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Screen;
import com.aware.providers.Screen_Provider;

public class MainActivity extends AppCompatActivity {
    private int screenTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Aware.startAWARE(this);
        Aware.setSetting(this, Aware_Preferences.STATUS_SCREEN,true);

        String URL = "content://com.aware.provider.screen/screen";
        Uri stats = Uri.parse(URL);
        String[] tableValues =
                {
                        Screen_Provider.Screen_Data._ID,
                        Screen_Provider.Screen_Data.TIMESTAMP,
                        Screen_Provider.Screen_Data.SCREEN_STATUS
                };
        String mSelectionCluase = null;
        String[] mSelectionArgs = null;
        String sortOrder = null;
        Cursor cursor = getContentResolver().query(stats,tableValues,mSelectionCluase,mSelectionArgs,sortOrder);
        if (cursor != null){
            cursor.moveToFirst();

            for (int i = 0;i< cursor.getCount();i++){
                screenTime += cursor.getInt(cursor.getColumnIndexOrThrow(Screen_Provider.Screen_Data.TIMESTAMP));
            }
        }
        TextView sTime = (TextView)findViewById(R.id.sampleText);
        sTime.setText(Integer.toString(screenTime));
    }

}