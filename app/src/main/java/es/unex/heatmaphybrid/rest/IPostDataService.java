package es.unex.heatmaphybrid.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.LocationsHeatMap;
import es.unex.heatmaphybrid.model.RequestHeatMap;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * Created by Javier on 18/10/2017.
 */
public interface IPostDataService {

    @Headers(
         "Accept: application/json"
    )
    @POST("/alpha/locations")
    void postLocations(@Body LocationsHeatMap locations, Callback<Object> callback);

    @POST("/alpha/heatmaps")
    void requestHeatMap(@Body RequestHeatMap requestHeatMap, Callback<Object> callback);

    @GET("/alpha/heatmaps/{requestId}")
    void getHeatMap(@Path(value = "requestId", encode = false) String requestId, Callback<List <LocationFrequency>> callback);

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();


    public static final RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("https://2dvuxld7v9.execute-api.us-east-1.amazonaws.com")
            .setConverter(new GsonConverter(gson))
            .build();

    /*public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://mj1vqv7ey6.execute-api.eu-central-1.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();*/


}
