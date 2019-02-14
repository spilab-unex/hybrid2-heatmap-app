package es.unex.heatmaphybrid.messagemanager;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbees.platform.NimbeesClient;
import com.nimbees.platform.NimbeesNotificationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.unex.heatmaphybrid.locationmanager.LocationManager;
import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.LocationsHeatMap;
import es.unex.heatmaphybrid.rest.IPostDataService;
import es.unex.heatmaphybrid.retrofit.APIService;
import es.unex.heatmaphybrid.retrofit.Common;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Javier on 10/10/2017.
 *  This NotificationManager lets handle a LocationMessage to do what you want with it
  */

public class NotificationManager extends NimbeesNotificationManager {

    /**
     * The context.
     */
    private Context mContext;

    /**
     * Endpoints to interact with the rest services
     */
    private IPostDataService rest;

    private static APIService apiService;

    public NotificationManager(Context context) {
        super(context);
        this.mContext = context;
        rest = IPostDataService.restAdapter.create(IPostDataService.class);

        apiService= Common.getServer();
    }

    /**
     * Receive a custom Message and we can do anything here with it
     *
     * @param idNotification the unique id of the notification
     * @param content the content of the message
     */
    @Override
    public void handleCustomMessage(
            long idNotification,
                String content,
                Map<String, String> aditionalContent) {


        //super.handleCustomMessage(idNotification, content, additionalContent);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        LocationMessage message = gson.fromJson(content, LocationMessage.class);

        Log.e("HEATMAP", "Mensaje: ");
        if (message.getKind() == NotificationKind.RequestLocation){
            RequestLocationMessage requestMsg = new Gson().fromJson(content, RequestLocationMessage.class);
            List <LocationFrequency> locations = LocationManager.getLocationHistory(requestMsg.getBeginDate(), requestMsg.getEndDate(),requestMsg.getLatitude(), requestMsg.getLongitude(), requestMsg.getRadius());
            Log.e("HEATMAP", "Location Requested " + locations.size());
            if (locations.size()>0) {
                this.postLocations(new LocationsHeatMap(requestMsg.getRequesterId(), locations));
            }
        } else{
            Log.e("HEATMAP", "Another kind of message: " +content);

        }

    }

    /*public void postLocations(List <LocationFrequency> locations){
        Call<String> call = rest.postLocations(locations);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("HEATMAP", "Locations posted" );
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ERROR: ", "Error posting the locations. " + t.getMessage());
            }
        });
    }*/


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

        rest.postLocations(locations, callback);



    }

    private static <T> List<List<T>> partition(List<T> input, int size) {
        List<List<T>> lists = new ArrayList<List<T>>();
        for (int i = 0; i < input.size(); i += size) {
            lists.add(input.subList(i, Math.min(input.size(), i + size)));
        }
        return lists;
    }
}
