package es.unex.heatmaphybrid.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocationBeanRealm extends RealmObject {

    private Double lat;

    private Double lng;

    private Date timestamp;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    @Override
    public String toString() {
        return "LocationBeanRealm{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
