package es.unex.heatmaphybrid.model;

/**
 * Created by Javier on 04/12/2017.
 */

public class Center
{
    private double longitude;

    private double latitude;

    public Center(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString()
    {
        return "Center [longitude = "+longitude+", latitude = "+latitude+"]";
    }
}

