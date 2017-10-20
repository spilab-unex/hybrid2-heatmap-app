package es.unex.heatmaphybrid.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import es.unex.heatmaphybrid.model.GetHeatMapMessage;
import es.unex.heatmaphybrid.model.LocationFrequency;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;


/**
 * Created by Javier on 18/10/2017.
 */
public interface IPostDataService {

    @Headers(
         "Accept: application/json"
    )


    @POST("test/locations")
    void postLocations(@Body List <LocationFrequency> locations, Callback<String> callback);

    @POST("/test/requestheatmap")
    void requestHeatMap(@Body GetHeatMapMessage heatMapMessage, Callback<String> callback);

    @POST("/test/heatmap")
    void getHeatMap(@Body String requestId, Callback<List <LocationFrequency>> callback);

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();


    public static final RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("https://mj1vqv7ey6.execute-api.eu-central-1.amazonaws.com")
            .setConverter(new GsonConverter(gson))
            .build();

    /*public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://mj1vqv7ey6.execute-api.eu-central-1.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();*/


}
