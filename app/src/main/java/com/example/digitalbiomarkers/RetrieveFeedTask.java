package com.example.digitalbiomarkers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;

 public class RetrieveFeedTask extends AsyncTask<Void,Void,String> {
    private Context context;
    public String textToAnalyse;

    public RetrieveFeedTask(Context context,String textToAnalyse){
        this.context = context;
        this.textToAnalyse = textToAnalyse;
    }
    @Override
    protected String doInBackground(Void... voids) {
        IamAuthenticator authenticator = new IamAuthenticator("Ufr3FL9wHAotv_f7QItN7Q_EKvUHkpK-B88J5USZWloR");
        ToneAnalyzer toneAnalyzer = new ToneAnalyzer("2017-09-21",authenticator);
        toneAnalyzer.setServiceUrl("https://api.us-south.tone-analyzer.watson.cloud.ibm.com/instances/94baf436-8819-49d9-af67-cf6c3fc3345c");
        ToneOptions toneOptions = new ToneOptions.Builder()
                .text(textToAnalyse)
                .build();

        ToneAnalysis toneAnalysis = toneAnalyzer.tone(toneOptions).execute().getResult();
        String msg = toneAnalysis.getDocumentTone().getTones().get(0).getToneName();
//        String msg = toneAnalysis.toString();
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        super.onPostExecute(msg);
        Toast.makeText(context, "Emotion:" + msg, Toast.LENGTH_SHORT).show();
//        MainActivity.setTone(msg);
    }
}
