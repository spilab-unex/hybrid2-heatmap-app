package es.unex.heatmaphybrid.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.LocationsHeatMap;
import es.unex.heatmaphybrid.model.RequestHeatMap;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by Javier on 18/10/2017.
 */
public interface IPostDataService {

    @Headers(
         "Accept: application/json"
    )
    @POST("/Version_0/locations")
    void postLocations(@Body LocationsHeatMap locations, Callback<Object> callback);

    @POST("/Version_0/heatmaps")
    void requestHeatMap(@Body RequestHeatMap requestHeatMap, Callback<Object> callback);

//    @GET("/Version_0/heatmaps/{requestId}")
//    void getHeatMap(@Path(value = "requestId", encode = false) String requestId, Callback<List <LocationFrequency>> callback);

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();


//    public static final RestAdapter restAdapter = new RestAdapter.Builder()
//            .setEndpoint("https://1v7ux9hog8.execute-api.eu-west-1.amazonaws.com")
//            .setConverter(new GsonConverter(gson))
//            .build();

    /*public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://mj1vqv7ey6.execute-api.eu-central-1.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();*/


}
