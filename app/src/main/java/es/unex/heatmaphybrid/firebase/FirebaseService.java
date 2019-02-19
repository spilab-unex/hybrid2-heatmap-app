package es.unex.heatmaphybrid.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.unex.heatmaphybrid.MainActivity;
import es.unex.heatmaphybrid.R;
import es.unex.heatmaphybrid.locationmanager.LocationManager;
import es.unex.heatmaphybrid.messagemanager.NotificationHelper;
import es.unex.heatmaphybrid.messagemanager.NotificationKind;
import es.unex.heatmaphybrid.messagemanager.RequestLocationMessage;
import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.LocationsHeatMap;
import es.unex.heatmaphybrid.retrofit.APIService;
import es.unex.heatmaphybrid.retrofit.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirebaseService extends FirebaseMessagingService {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    private static APIService apiService= Common.getServer();

    public FirebaseService() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("Firebase Token", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String TAG = "FirebaseService: ";

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            FirebaseData notification = gson.fromJson(String.valueOf(data), FirebaseData.class);


            //Check the type of message
            if (notification.getRequest() != null) {

                processRequest(notification);
                Log.e(TAG, "Locations received from sender.");

            } else {
                if (notification.getReply() != null) {
                    Log.e(TAG, "Reply received.");
                    processReply(notification);

                } else {
                    Log.e(TAG, "Another kind of message: ");
                }

            }


        }

    }

    public void postLocations(LocationsHeatMap locations) {
        //Firebase 3 step

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Log.e("JSON POST LOCATIONS", gson.toJson(locations));
        Callback<Object> callback = new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }


        };


        apiService.postLocations(locations).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(response.isSuccessful())
                    Log.i("HEATMAP", "Locations posted" + response.body());

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("ERROR: ", "Error posting the locations. " + t.getMessage());
            }
        });


    }

    private void processReply(FirebaseData notification) {
        LocationManager.storeLocations(notification.getReply().getLocationList());
    }

    private void processRequest(FirebaseData notification) {
        List <LocationFrequency> locations = LocationManager.getLocationHistory(notification.getRequest().getBeginDate(), notification.getRequest().getEndDate(), notification.getRequest().getLatitude(), notification.getRequest().getLongitude(), notification.getRequest().getRadius());
        Log.e("HEATMAP", "Location Requested " + locations.size());
        if (locations.size()>0) {
            Log.e("Notification Received",notification.getRequest().toString());
            this.postLocations(new LocationsHeatMap(notification.getRequest().getRequesterId(), locations));
        }
    }

    private static <T> List<List<T>> partition(List<T> input, int size) {
        List<List<T>> lists = new ArrayList<List<T>>();
        for (int i = 0; i < input.size(); i += size) {
            lists.add(input.subList(i, Math.min(input.size(), i + size)));
        }
        return lists;
    }

    private void showNotification(String title, String body) {

        //Intent to open APP when click in the notification.
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID="1";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel= new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription ("Android Server");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setContentIntent(resultPendingIntent).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.common_full_open_on_phone).setContentTitle(title).setContentText(body);
        notificationManager.notify((new Random().nextInt()),notificationBuilder.build());



    }

}
