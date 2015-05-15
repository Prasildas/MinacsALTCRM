package com.tspl.minacsaltcrm.ClassObject;

import java.util.HashMap;
import java.util.List;

/**
 * Model class for holding the location and distance of places
 * Created by Vishnu Mohan on 30-04-2015.
 */
public class RouteDetails {
    public List<List<HashMap<String, String>>> routes;
    public String duration;
    public float distance;
    public double latitude;
    public double longitude;
    public String placeName;
    public String address;
    public String country;
    public int id;


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
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

    public List<List<HashMap<String, String>>> getRoutes() {
        return routes;
    }

    public void setRoutes(List<List<HashMap<String, String>>> routes) {
        this.routes = routes;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
