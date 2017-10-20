package es.unex.heatmaphybrid.messagemanager;

import java.util.Date;

/**
 * Created by Javier on 10/10/2017.
 */

public class RequestLocationMessage extends  LocationMessage{

    Date beginDate;

    Date endDate;

    Double latitude;

    Double longitude;

    Double radius;



    public RequestLocationMessage(String requesterId, Date begin, Date end, double latitude, double longitude, double radius) {
        this.requesterId = requesterId;
        this. kind = NotificationKind.RequestLocation;
        beginDate = begin;
        endDate = end;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;

    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
