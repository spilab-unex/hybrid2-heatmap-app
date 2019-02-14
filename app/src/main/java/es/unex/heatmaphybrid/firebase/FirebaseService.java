package es.unex.heatmaphybrid.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.unex.heatmaphybrid.locationmanager.LocationManager;
import es.unex.heatmaphybrid.messagemanager.NotificationHelper;
import es.unex.heatmaphybrid.messagemanager.NotificationKind;
import es.unex.heatmaphybrid.messagemanager.RequestLocationMessage;
import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.LocationsHeatMap;
import es.unex.heatmaphybrid.retrofit.APIService;
import es.unex.heatmaphybrid.retrofit.Common;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


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

        Callback<Object> callback = new Callback<Object>() {

            @Override
            public void success(Object s, Response response) {
                Log.i("HEATMAP", "Locations posted" );
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("ERROR: ", "Error posting the locations. " + retrofitError.getMessage()+ " " + retrofitError.getUrl() + " " + retrofitError.getBody());
            }
        };

        //rest.postLocations(locations, callback);
        //Firebase 3 step
        apiService.postLocations(locations,callback);


    }

    private void processReply(FirebaseData notification) {
        LocationManager.storeLocations(notification.getReply().getLocationList());
    }

    private void processRequest(FirebaseData notification) {
        List <LocationFrequency> locations = LocationManager.getLocationHistory(notification.getRequest().getBeginDate(), notification.getRequest().getEndDate(), notification.getRequest().getLatitude(), notification.getRequest().getLongitude(), notification.getRequest().getRadius());
        Log.e("HEATMAP", "Location Requested " + locations.size());
        if (locations.size()>0) {
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

}
