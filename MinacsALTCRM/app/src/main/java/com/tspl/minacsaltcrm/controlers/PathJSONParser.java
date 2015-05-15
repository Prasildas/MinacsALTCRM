package com.tspl.minacsaltcrm.controlers;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathJSONParser {

    public class TripDetails {
        public String duration;
        public float distance;
    }

    HashMap<String, String> tripDetails = new HashMap<String, String>();

    /**
     * to find the shortest distance using travel distance
     * @param jObject
     * @return
     * @throws Exception
     */
    public TripDetails getTripDetails(JSONObject jObject) throws Exception {

        TripDetails tripDetails = new TripDetails();

        JSONArray routes = jObject.getJSONArray("routes");
        JSONObject route = (JSONObject) routes.get(0);
        JSONArray legs = route.getJSONArray("legs");
        JSONObject leg = (JSONObject) legs.get(0);
        tripDetails.duration = leg.getJSONObject("duration").getString("text");
        String distance = leg.getJSONObject("distance").getString("text");

        if (distance != null && distance != "") {
            distance = distance.substring(0, distance.indexOf("km")).trim();
            distance = distance.replace(",", "");
        }
        tripDetails.distance = Float.parseFloat(distance);

        return tripDetails;
    }

    public List<List<HashMap<String, String>>> parse(JSONObject jObject, ArrayList<Step> steps) {
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        Step step = new Step();
                        step.distance = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("distance")).get("text");
                        Log.e("distance", step.distance);
                        step.duration = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("duration")).get("text");

                        step.start_location = new LatLng(((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).getDouble("lat"),
                                ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).getDouble("lng"));

                        step.end_location = new LatLng(((JSONObject) ((JSONObject) jSteps.get(k)).get("end_location")).getDouble("lat"),
                                ((JSONObject) ((JSONObject) jSteps.get(k)).get("end_location")).getDouble("lng"));
                        step.travel_mode = (String) (((JSONObject) jSteps.get(k)).get("travel_mode"));
                        step.html_instructions = (String) (((JSONObject) jSteps.get(k)).get("html_instructions"));
                        step.points = list;
                        steps.add(step);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }

                    }
                    routes.add(path);

                }

            }

        } catch (JSONException e) {

        } catch (Exception e) {

        }
        return routes;
    }

    public class Step {
        public String distance;
        public String duration;
        public LatLng end_location;
        public LatLng start_location;
        public String travel_mode;
        public String html_instructions;
        public List<LatLng> points;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
