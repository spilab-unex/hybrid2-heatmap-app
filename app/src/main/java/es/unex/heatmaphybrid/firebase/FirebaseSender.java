package es.unex.heatmaphybrid.firebase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
 * Class to generate the Firebase Cloud Messaging body request.
 */
public class FirebaseSender {

    /*
     * Parameter to indicate the recipient (token or topic).
     */
    @SerializedName("to")
    @Expose
    public String to;

    /*
     * Parameter that indicates the "data" field in the body of the Firebase.
     */
    @SerializedName("data")
    @Expose
    public FirebaseData data;

    public FirebaseSender() {
    }

    public FirebaseSender(String to, FirebaseData data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public FirebaseData getData() {
        return data;
    }

    public void setData(FirebaseData data) {
        this.data = data;
    }


}
