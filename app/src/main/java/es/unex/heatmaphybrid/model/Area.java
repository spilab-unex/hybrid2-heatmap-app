package es.unex.heatmaphybrid.model;

/**
 * Created by Javier on 04/12/2017.
 */

public class Area
{
    private Center center;

    private double radius;

    public Area(Center center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Center getCenter ()
    {
        return center;
    }

    public void setCenter (Center center)
    {
        this.center = center;
    }

    public double getRadius ()
    {
        return radius;
    }

    public void setRadius (double radius)
    {
        this.radius = radius;
    }

    @Override
    public String toString()
    {
        return "Area [center = "+center+", radius = "+radius+"]";
    }
}
