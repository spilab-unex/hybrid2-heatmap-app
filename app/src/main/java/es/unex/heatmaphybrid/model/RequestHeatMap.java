package es.unex.heatmaphybrid.model;

import java.util.Date;

/**
 * Created by Javier on 04/12/2017.
 */

public class RequestHeatMap {
    private Area area;

    private String idRequester;

    private Period period;

    public RequestHeatMap(String idRequester, Date begin, Date end, double latitude, double longitude, double radius) {
        this.area = new Area (new Center(latitude, longitude), radius);
        this.idRequester = idRequester;
        this.period = new Period (begin, end);
    }

    public Area getArea ()
    {
        return area;
    }

    public void setArea (Area area)
    {
        this.area = area;
    }

    public String getIdRequester ()
    {
        return idRequester;
    }

    public void setIdRequester (String idRequester)
    {
        this.idRequester = idRequester;
    }

    public Period getPeriod ()
    {
        return period;
    }

    public void setPeriod (Period period)
    {
        this.period = period;
    }

    @Override
    public String toString()
    {
        return "RequestHeatMap [area = "+area+", idRequester = "+idRequester+", period = "+period+"]";
    }
}
