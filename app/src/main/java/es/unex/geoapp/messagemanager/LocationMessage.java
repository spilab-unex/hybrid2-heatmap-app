package es.unex.geoapp.messagemanager;

/**
 * Created by Javier on 11/10/2017.
 */

public class LocationMessage {
    /** Id of the user in nimBees*/
    String senderId;

    NotificationKind kind;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public NotificationKind getKind() {
        return kind;
    }

    public void setKind(NotificationKind kind) {
        this.kind = kind;
    }
}
