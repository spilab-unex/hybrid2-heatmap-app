package es.unex.heatmaphybrid.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import es.unex.heatmaphybrid.model.LocationFrequency;

public class ResponseLambda {

    @SerializedName("statusCode")
    @Expose
    public Integer statusCode;
    @SerializedName("headers")
    @Expose
    public Headers headers;
    @SerializedName("body")
    @Expose
    public String body;
    @SerializedName("isBase64Encoded")
    @Expose
    public Boolean isBase64Encoded;


    public class Headers {

        @SerializedName("Content-Type")
        @Expose
        public String contentType;

    }

}
