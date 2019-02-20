package es.unex.heatmaphybrid.locationmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import es.unex.heatmaphybrid.MainActivity;
import es.unex.heatmaphybrid.model.LocationBeanRealm;
import es.unex.heatmaphybrid.rest.IPostDataService;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmResults;
/*
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
*/

/**
 * Created by Javier on 18/10/2017.
 */

public class LocationService extends Service {
    /**
     * Seconds to send
     */
    //long MILISECONDS_REFRESH = 150000;
    long MILISECONDS_REFRESH = 50000;

    private Timer timer;

    private GPSTracker gps;

    PowerManager.WakeLock wakeLock;

    //private IPostDataService rest;

    private Realm realm;

    private boolean startRealm;

    @Override
    public void onCreate() {

        this.startRealm = false;

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        wakeLock.acquire();

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();



    }

    /*Sends Location to Main for add the point in the map with location by broadcast*/
    private void sendLocation() {
        if(gps.getLatitude()!=0.0 && gps.getLongitude()!=0.0){
            Intent intent = new Intent();
            intent.putExtra("lat", gps.getLatitude());
            intent.putExtra("long",gps.getLongitude());
            intent.setAction("NOW");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Tracking the location", Toast.LENGTH_SHORT).show();
        if (gps == null) {
            gps = new GPSTracker(this);
        }

        sendLocation();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                postGPSPosition();
            }
        }, 0, MILISECONDS_REFRESH);


        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        stopSelf();

        if (wakeLock != null) {
            wakeLock.release();
        }

        super.onDestroy();

        Toast.makeText(this, "Location tracking stopped", Toast.LENGTH_SHORT).show();
    }

    public void postGPSPosition() {

        // To start Realm once
        if (!this.startRealm) {
            Log.i("HEATMAP-INIT", "Starting Realm...");
            try {
                Realm.init(this);
                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                        .name("Database.realm")
                        .schemaVersion(0)
                        .deleteRealmIfMigrationNeeded()
                        .build();
                Realm.setDefaultConfiguration(realmConfiguration);
                this.startRealm = true;
                Log.i("HEATMAP-INIT", "Realm started successfully");
            } catch (Exception e) {
                Log.e("HEATMAP-INIT", "Error during start Realm: " + e.getMessage());
            }

        }

        if (gps.canGetLocation()) {

            // Store the location in the Realm database
            Realm realm=Realm.getDefaultInstance();
            realm.beginTransaction();

            LocationBeanRealm lbr = realm.createObject(LocationBeanRealm.class);
            lbr.setLat(gps.getLatitude());
            lbr.setLng(gps.getLongitude());
            Date curDateTime = Calendar.getInstance().getTime();
            lbr.setTimestamp(curDateTime);

            realm.commitTransaction();

            Log.i("HEATMAP-LBR", lbr.toString());

        } else {
            //gps.showSettingsAlert();
            Log.e("HEATMAP", "Can not get the location");
        }
    }

}