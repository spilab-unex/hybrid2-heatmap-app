package es.unex.heatmaphybrid.retrofit;



import java.util.List;

import es.unex.heatmaphybrid.firebase.FirebaseResponse;
import es.unex.heatmaphybrid.firebase.FirebaseSender;
import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.LocationsHeatMap;
import es.unex.heatmaphybrid.model.RequestHeatMap;
import retrofit2.Call;
import retrofit.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

//    @Headers({
//            "Content-Type:application/json",
//            "Authorization:key=AAAAXKsK2vU:APA91bHoA6hUn6DrrRGI9TBSvA7Zft3SQ9QOuuCR7R84eK_oLWy7K0eFUEf6OaNLQfiZkV8owSUHImntDx3rpZ2PVjQr8MqYawoIFJiwOlRMUHwl8qGKOyiTZS_bfmPT2HDi-zuPgC2p"
//    })
//    @POST( "fcm/send")
//    Call<FirebaseResponse> sendNotification(@Body FirebaseSender body);


    @Headers(
            "Accept: application/json"
    )
    @POST("/alpha/locations")
    void postLocations(@Body LocationsHeatMap locations, Callback<Object> callback);

    @Headers(
            "Accept: application/json"
    )
    @POST("/alpha/heatmaps")
    void requestHeatMap(@Body RequestHeatMap requestHeatMap, Callback<Object> callback);

    @Headers(
            "Accept: application/json"
    )
    @GET("/alpha/heatmaps/{requestId}")
    void getHeatMap(@Path(value = "requestId") String requestId, Callback<List<LocationFrequency>> callback);

}