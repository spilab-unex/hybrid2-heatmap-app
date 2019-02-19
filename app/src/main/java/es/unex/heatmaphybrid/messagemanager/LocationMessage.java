package es.unex.heatmaphybrid.messagemanager;

/**
 * Created by Javier on 11/10/2017.
 */

public class LocationMessage {
    /** Id of the user in nimBees*/
    String idRequester;

    String topic;

    NotificationKind kind;

    public String getRequesterId() {
        return idRequester;
    }

    public void setRequesterId(String requesterId) {
        this.idRequester = requesterId;
    }

    public NotificationKind getKind() {
        return kind;
    }

    public void setKind(NotificationKind kind) {
        this.kind = kind;
    }
}
