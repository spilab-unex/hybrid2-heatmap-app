package es.unex.heatmaphybrid.messagemanager;

/**
 * Created by Javier on 11/10/2017.
 */

public class LocationMessage {
    /** Id of the user in nimBees*/
    String requesterId;

    NotificationKind kind;

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public NotificationKind getKind() {
        return kind;
    }

    public void setKind(NotificationKind kind) {
        this.kind = kind;
    }
}
