package es.unex.heatmaphybrid.firebase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FirebaseResponse {

    @SerializedName("multicast_id")
    @Expose
    public Integer multicastId;
    @SerializedName("success")
    @Expose
    public Integer success;
    @SerializedName("failure")
    @Expose
    public Integer failure;
    @SerializedName("canonical_ids")
    @Expose
    public Integer canonicalIds;
    @SerializedName("results")
    @Expose
    public List<Result> results;

    public class Result {

        @SerializedName("message_id")
        @Expose
        public String messageId;

    }

    public FirebaseResponse() {
    }

    public FirebaseResponse(Integer multicastId, Integer success, Integer failure, Integer canonicalIds, List<Result> results) {
        this.multicastId = multicastId;
        this.success = success;
        this.failure = failure;
        this.canonicalIds = canonicalIds;
        this.results = results;
    }

    public Integer getMulticastId() {
        return multicastId;
    }

    public Integer getSuccess() {
        return success;
    }

    public Integer getFailure() {
        return failure;
    }

    public Integer getCanonicalIds() {
        return canonicalIds;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setMulticastId(Integer multicastId) {
        this.multicastId = multicastId;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public void setFailure(Integer failure) {
        this.failure = failure;
    }

    public void setCanonicalIds(Integer canonicalIds) {
        this.canonicalIds = canonicalIds;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
