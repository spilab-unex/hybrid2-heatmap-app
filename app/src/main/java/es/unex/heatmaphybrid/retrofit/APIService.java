package es.unex.heatmaphybrid.retrofit;



import java.util.List;


import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.LocationsHeatMap;
import es.unex.heatmaphybrid.model.RequestHeatMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {


    @Headers(
            "Accept: application/json"
    )
    @POST("/Version_0/locations")
    Call<Object> postLocations(@Body LocationsHeatMap locations);

    @Headers(
            "Accept: application/json"
    )
    @POST("/Version_0/heatmaps")
    Call<Object> requestHeatMap(@Body RequestHeatMap body);


    @GET("/Version_0/heatmaps/")
    Call<ResponseLambda> getHeatMap(@Query(value = "idRequester") String idRequester/*, Callback<List<LocationFrequency>> callback*/);

}
