package com.tspl.minacsaltcrm;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tspl.minacsaltcrm.Popup.MapsInfoPopup;
import com.tspl.minacsaltcrm.controlers.GPSTracker;
import com.tspl.minacsaltcrm.controlers.HttpConnection;
import com.tspl.minacsaltcrm.controlers.PathJSONParser;
import com.tspl.minacsaltcrm.ClassObject.RouteDetails;
import com.tspl.minacsaltcrm.views.DatePickerFragment;
import com.tspl.minacsaltcrm.views.LoadingDialog;
import com.tspl.minacsaltcrm.views.TimePickerFragment;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends BaseFragmentActivity implements ActionBar.TabListener, View.OnClickListener {

    private static final String TAG = "MapsActivity";
    private static final int ENABLE_LOCATIONSERVICES = 2;
    MapsInfoPopup mapsInfoPopup;
    private GPSTracker gpsTracker;
    private Location myLocation;
    private LoadingDialog dialog;
    private LocationManager locationManager;
    private ArrayList<PathJSONParser.Step> steps = new ArrayList<PathJSONParser.Step>();
    private ReadTask readTask;
    private RouteDetails nearestLocation;
    boolean cameraTabInitFlag = false;
    private BackgroundAsync backgroundAsync;
    public static boolean reqReLoadingMap = false;
    protected Button button_home, button_myProfile, button_social, button_contact;

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    GoogleMap map;
    PopupWindow popupWindow;
    private Handler handler;
    private List<RouteDetails> routeDetailsList;
    private Context context;
    private Marker myLocationMarker;
    ActionBar actionBar;

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        super.bindFooterViews(MapsActivity.this);

        context = MapsActivity.this;
        handler = new Handler();
        gpsTracker = new GPSTracker(MapsActivity.this);
        readTask = new ReadTask();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        button_home = (Button) findViewById(R.id.button_home);
        button_myProfile = (Button) findViewById(R.id.button_myProfile);
        button_social = (Button) findViewById(R.id.button_social);
        button_contact = (Button) findViewById(R.id.button_contact);

        if (button_home != null)
            button_home.setOnClickListener(this);
        if (button_myProfile != null)
            button_myProfile.setOnClickListener(this);
        if (button_social != null)
            button_social.setOnClickListener(this);
        if (button_contact != null)
            button_contact.setOnClickListener(this);


        try {
            mapsInfoPopup = new MapsInfoPopup(MapsActivity.this);
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker arg0) {
                    if (arg0.getTitle().equalsIgnoreCase("You are Here")) { // if marker source is clicked
                        mapsInfoPopup.addDetails(arg0.getTitle(), arg0.getSnippet());
                        mapsInfoPopup.setVisible(true);
                        return true;
                    }
                    return false;
                }

            });
        } catch (Exception e) {

        }
        // Create the adapter that will return a fragment for each of the primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        backgroundAsync = new BackgroundAsync();
        backgroundAsync.execute();
    }

    Intent intent;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_myProfile:
                intent = new Intent(context, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.button_social:
                intent = new Intent(context, SocialActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.button_home:
                Intent intentHome = new Intent(context, HomeActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                finish();
                break;

            case R.id.button_contact:
                intent = new Intent(context, ContactActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            default:
                break;
        }
    }


    /**
     * To display the date picker dialog and get date
     */
    public String showDateDialog() {
        final String selectedDate[] = new String[1];
        android.app.FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(fm, "date");

        datePickerFragment.setDialogDataConnector(new DatePickerFragment.UpdateFromDateDialog() {
            @Override
            public void setDate(String[] date) {
                selectedDate[0] = date[0];
                mapsInfoPopup.etSelectDate.setText(selectedDate[0]);
            }
        });
        return selectedDate[0];
    }

    /**
     * To check if internet is available
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Function to mock list of locations
     *
     * @return
     */
    private List<RouteDetails> getLocationListAndPlaceMarker() {

        // Now place markers
        for (RouteDetails details : allLocs) {
            addMarkerWithCameraZooming(details.getLatitude(), details.getLongitude(), details.getPlaceName(), "", false, false);
        }
        return allLocs;
    }

    /**
     * addMarkerWithCameraZooming()
     * adding marker in google map
     * @param latitude
     * @param longitude
     * @param title
     * @param snippet
     * @param dragabble
     * @param isMyLocation
     */
    private void addMarkerWithCameraZooming(double latitude, double longitude,
                                            String title, String snippet, boolean dragabble, boolean isMyLocation) {
        LatLng current_latlng = new LatLng(latitude, longitude);
        try {
            Marker marker = map.addMarker(new MarkerOptions().position(current_latlng)
                    .title(title).snippet(snippet).draggable(dragabble));

            if (isMyLocation) {
                myLocationMarker = marker;
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
            if (isMyLocation) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude)).zoom(12).tilt(30)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Map<String, List<RouteDetails>> mapsListOfLocs = new HashMap<String, List<RouteDetails>>();
    List<String> allcountries = new ArrayList<String>();

    List<RouteDetails> allLocs;

    /**
     * BackgroundAsync fetches the locations from the server
     */
    public class BackgroundAsync extends AsyncTask<Void, Void, Integer> {
        private String employeeID;
        private int pageNo;
        ProgressDialog pd;

        public BackgroundAsync() {
            pd = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            pd.setCancelable(false);
            pd.setMessage("Please wait...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String result = "";
            try {
                allLocs = new ArrayList<RouteDetails>();
                result = new SoapHandler().getMapDetails();
                Pr.ln("result map " + result);
                JSONObject locObj = new JSONObject(result);
                if (locObj.has("Acknowledge") && locObj.getInt("Acknowledge") == 1) {
                    if (locObj.has("Locations")) {
                        JSONArray locationsJsonArr = locObj.getJSONArray("Locations");
                        for (int i = 0; i < locationsJsonArr.length(); i++) {
                            JSONObject locationJson = locationsJsonArr.getJSONObject(i);
                            RouteDetails location = new RouteDetails();
                            location.id = locationJson.getInt("Id");
                            location.placeName = locationJson.getString("Name");
                            location.address = locationJson.getString("Address");
                            location.latitude = locationJson.getDouble("Latitude");
                            location.longitude = locationJson.getDouble("Longitude");
                            location.country = locationJson.getString("Country");
                            Set keys = mapsListOfLocs.keySet();
                            if (keys.contains(location.country)) {
                                List<RouteDetails> routes = mapsListOfLocs.get(location.country);
                                routes.add(location);
                                mapsListOfLocs.put(location.country, routes);
                            } else {
                                List<RouteDetails> routes = new ArrayList<RouteDetails>();
                                routes.add(location);
                                mapsListOfLocs.put(location.country, routes);
                            }
                            allLocs.add(location);
                        }
                    }
                    return 1;
                }
            } catch (JSONException je) {

            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer i) {
            if (pd.isShowing()) {
                pd.dismiss();
            }

            if (i != -1) {
                Set keys = mapsListOfLocs.keySet();
                Iterator itr = keys.iterator();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    allcountries.add(key);
                    actionBar.addTab(
                            actionBar.newTab()
                                    .setText(key)
                                    .setTabListener(MapsActivity.this));
                }
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    try {
                        myLocation = gpsTracker.getLocation();
                        if (isNetworkAvailable()) {
                            //Get the list of locations and place a marker on every place
                            routeDetailsList = getLocationListAndPlaceMarker();
                            //Place a marker on my location
                            addMarkerWithCameraZooming(myLocation.getLatitude(), myLocation.getLongitude(), "You are Here", "", false, true);
                            readTask.execute(routeDetailsList);
                        } else {
                            Toast.makeText(context, "Internet connection currently unavailable", Toast.LENGTH_LONG).show();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        enabelLocationServices();
                    }

                } else {
                }

            }
            super.onPostExecute(i);
        }
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     * Its not showing any data now.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            Fragment fragment = new MapsFragment();
            Bundle args = new Bundle();
            args.putInt(MapsFragment.ARG_SECTION_NUMBER, i);
            fragment.setArguments(args);
            return fragment;

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";

                default:
                    return "";

            }

        }
    }

    /**
     * ReadTask finding the nearest locations
     */
    private class ReadTask extends AsyncTask<List<RouteDetails>, Void, List<RouteDetails>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog = new LoadingDialog(MapsActivity.this,
                            "Loading...",
                            "Please wait while we find the nearest office location from you.");
                    dialog.setVisible(true);
                }
            });

        }

        @Override
        protected List<RouteDetails> doInBackground(List<RouteDetails>... locationList) {
            String data;
            JSONObject jObject;
            LatLng end;

            LatLng start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            try {


                HttpConnection http = new HttpConnection();
                for (RouteDetails route : locationList[0]) {
                    end = new LatLng(route.getLatitude(), route.getLongitude());
                    data = http.readUrl(getMapsApiDirectionsUrl(start, end));

                    try {
                        jObject = new JSONObject(data);
                        PathJSONParser parser = new PathJSONParser();

                        steps.clear();

                        PathJSONParser.TripDetails tripDetails = new PathJSONParser().new TripDetails();
                        tripDetails = parser.getTripDetails(jObject);

                        route.setDistance(tripDetails.distance);
                        route.setDuration(tripDetails.distance + "");
                    } catch (Exception e) {
                    }
                }

            } catch (Exception e) {
            }
            return locationList[0];
        }

        @Override
        protected void onPostExecute(List<RouteDetails> routeList) {
            super.onPostExecute(routeList);
            nearestLocation = new RouteDetails();
            float shortestDistance = routeList.get(0).getDistance();
            nearestLocation = routeList.get(0);
            Set keys = mapsListOfLocs.keySet();
            int tabSelect = allcountries.indexOf(nearestLocation.country);
            actionBar.setSelectedNavigationItem(tabSelect);
            float distance;


            try {
                for (RouteDetails routeDetails : routeList) {
                    distance = routeDetails.getDistance();
                    if (distance < shortestDistance && distance > 0) {
                        shortestDistance = distance;
                        nearestLocation = routeDetails;
                    }
                }
                mapsInfoPopup.setLocationId(nearestLocation.id);
                mapsInfoPopup.setData(nearestLocation.placeName, nearestLocation.address);
                SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
                mapsInfoPopup.setEmployeeID(preferences.getString(AppConstants.employeeID, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).zoom(12).tilt(30)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /**
     * MapsFragment fragment representing a section of the app,
     */
    public static class MapsFragment extends Fragment {

        public static String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.textview_heading, container, false);
            Bundle args = getArguments();
            int index = args.getInt(ARG_SECTION_NUMBER);
            return rootView;
        }

    }

    @Override
    public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
        if (!cameraTabInitFlag) {
            cameraTabInitFlag = true;
        } else {
            List<RouteDetails> lst = mapsListOfLocs.get(tab.getText());
            if (lst.size() > 0) {
                RouteDetails loc1 = mapsListOfLocs.get(tab.getText()).get(0);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(loc1.getLatitude(), loc1.getLongitude())).zoom(12).tilt(30)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }


    @Override
    public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {

    }

    /**
     * Function to get the google places api url. Using which the places are
     * parsed
     *
     * @param startLocation
     * @param endLocation
     * @return
     */
    private String getMapsApiDirectionsUrl(LatLng startLocation, LatLng endLocation) {
        String startPosition = startLocation.latitude + "," + startLocation.longitude;
        String endPosition = endLocation.latitude + "," + endLocation.longitude;
        String sensor = "sensor=false";
        String modeString = "mode=DRIVING";
        String unit = "units=matric";
        String params = "origin=" + startPosition + "&destination="
                + endPosition + "&" + sensor + "&" + unit + "&" + modeString;
        String output = "json";
        String apiKey = "AIzaSyDBYA1s6nqHl4B0heZxhMcSTgQqa6kXmoE";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params + apiKey;
//        Log.e(TAG, url);
        return url;
    }

    /**
     * Ask user to enable location services
     */
    private void enabelLocationServices() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder
                .setMessage("Location services are disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.dismiss();
                                reqReLoadingMap = true;
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent, ENABLE_LOCATIONSERVICES);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Pr.ln(TAG+ "Resuming...");
        if (reqReLoadingMap) {
            Pr.ln(TAG+ "Require loading map...");
            gpsTracker = new GPSTracker(MapsActivity.this);
            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    try {
                        myLocation = gpsTracker.getLocation();
                        Pr.ln(TAG+ "My Location " + myLocation.getLatitude() + ", " + myLocation.getLongitude());
                        if (isNetworkAvailable()) {
                            //Place a marker on my location

                            //Get the list of locations and place a marker on every place
                            routeDetailsList = getLocationListAndPlaceMarker();

                            addMarkerWithCameraZooming(myLocation.getLatitude(), myLocation.getLongitude(), "You are Here", "", false, true);

                            readTask.execute(routeDetailsList);
                        } else {
                            Toast.makeText(context, "Internet connection currently unavailable", Toast.LENGTH_LONG).show();
                        }
                    } catch (NullPointerException e) {
                        enabelLocationServices();
                    }

                } else {

                }
                reqReLoadingMap = false;
            } catch (Exception e) {

            }
        }
    }

}