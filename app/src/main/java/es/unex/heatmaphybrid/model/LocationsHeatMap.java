package es.unex.heatmaphybrid.model;

import java.util.List;

/**
 * Created by Javier on 04/12/2017.
 */

public class LocationsHeatMap {

    String idRequester;

    List<LocationFrequency> locations;

    public LocationsHeatMap(String idRequester, List<LocationFrequency> locations) {
        this.idRequester = idRequester;
        this.locations = locations;
    }

    public String getIdRequester() {
        return idRequester;
    }

    public void setIdRequester(String idRequester) {
        this.idRequester = idRequester;
    }

    public List<LocationFrequency> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationFrequency> locations) {
        this.locations = locations;
    }
}
