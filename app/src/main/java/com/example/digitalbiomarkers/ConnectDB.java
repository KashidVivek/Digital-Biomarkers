package com.example.digitalbiomarkers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Https;
import com.aware.utils.SSLManager;

import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public class ConnectDB extends AsyncTask<Void,Void,Boolean> {
    private Context context;
    public ConnectDB(Context context){
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        SSLManager.handleUrl(context,"https://api.awareframework.com/index.php",true);
        Hashtable<String, String> device_ping = new Hashtable<>();
        device_ping.put(Aware_Preferences.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
        device_ping.put("ping", String.valueOf(System.currentTimeMillis()));
        device_ping.put("platform", "android");
        try {
            PackageInfo package_info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            device_ping.put("package_name", package_info.packageName);
            if (package_info.packageName.equals("com.aware.phone")) {
                device_ping.put("package_version_code", String.valueOf(package_info.versionCode));
                device_ping.put("package_version_name", String.valueOf(package_info.versionName));
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        try {
            new Https(SSLManager.getHTTPS(context, "https://api.awareframework.com/index.php")).dataPOST("https://api.awareframework.com/index.php/awaredev/alive", device_ping, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean msg) {
        super.onPostExecute(msg);
        Toast.makeText(context, "Connection:" + Boolean.toString(msg), Toast.LENGTH_SHORT).show();
    }
}
