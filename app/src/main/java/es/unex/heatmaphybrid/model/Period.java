package es.unex.heatmaphybrid.model;

import java.util.Date;

/**
 * Created by Javier on 04/12/2017.
 */

public class Period
{
    private Date to;

    private Date from;

    public Period(Date from, Date to) {
        this.to = to;
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    @Override
    public String toString()
    {
        return "Perido [to = "+to+", from = "+from+"]";
    }
}