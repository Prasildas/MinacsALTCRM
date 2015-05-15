package com.tspl.minacsaltcrm.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tspl.minacsaltcrm.AppConstants;
import com.tspl.minacsaltcrm.ClassObject.Holiday;
import com.tspl.minacsaltcrm.Popup.ChangeMyOptionalHolidayPopup;
import com.tspl.minacsaltcrm.R;
import com.tspl.minacsaltcrm.adapters.ChangeMyHolidayGridAdapter;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t0396 on 5/6/2015.
 */
public class ChangeMyOptionalHoliday extends Fragment {
    Context context;
    Activity pActivity;
    View rootView;
    TextView tvNoHolidays;
    SharedPreferences preferences;
    private GridView gridView_holidays;
    ChangeMyOptionalHolidayPopup changeMyOptionalHolidayPopup;

    List<Holiday> holidays;
    ChangeMyHolidayGridAdapter holidayGridAdapter;

    public ChangeMyOptionalHoliday() {

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        pActivity = activity;
        context = getActivity();
        super.onAttach(activity);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.change_optionalholiday, container,
                false);
        initViews(rootView);
        loadListeners();
        loadData();
       return rootView;
    }
    /*
            initialise views
             */
    private void initViews(View rootView) {
        preferences = getActivity().getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, Context.MODE_PRIVATE);
        holidays = new ArrayList<Holiday>();
        holidayGridAdapter = new ChangeMyHolidayGridAdapter(context);
        tvNoHolidays = (TextView) rootView.findViewById(R.id.tvNoHolidays);
        gridView_holidays = (GridView) rootView.findViewById(R.id.gridView_holidays);
        gridView_holidays.setAdapter(holidayGridAdapter);
        changeMyOptionalHolidayPopup = new ChangeMyOptionalHolidayPopup(context);
        changeMyOptionalHolidayPopup.setEmployeeID(preferences.getString(AppConstants.employeeID, ""));
    }
    /**
     * binding listeners
     */
    private void loadListeners() {
        gridView_holidays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Holiday holiday = holidayGridAdapter.getItem(position);
                if (holiday.isOptional) {
                    String title = "Change Optional holiday ( " + holiday.description + " ) to ";
                    changeMyOptionalHolidayPopup.setData(title);
                    changeMyOptionalHolidayPopup.setSelectedHoliday(holiday);
                    changeMyOptionalHolidayPopup.setVisible(true);
                }
            }
        });
    }

    /**
     * Fetching optional holiday list
     */
    private void loadData() {

        BackgroundAsync backgroundAsync = new BackgroundAsync(preferences.getString(AppConstants.employeeID, ""));
        backgroundAsync.execute();
    }


    public class BackgroundAsync extends AsyncTask<Void, Void, String> {
        private String employeeID;
        ProgressDialog pd;

        public BackgroundAsync(String _employeeID) {
            employeeID = _employeeID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setMessage("Please wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().getHolidays(employeeID);
                JSONObject holidaysObj = new JSONObject(result);
                if (holidaysObj.has("Acknowledge") && holidaysObj.getInt("Acknowledge") == 1) {
                    if (holidaysObj.has("Holidays")) {
                        JSONArray holidaysJsonArr = holidaysObj.getJSONArray("Holidays");
                        for (int i = 0; i < holidaysJsonArr.length(); i++) {
                            JSONObject holidayJson = holidaysJsonArr.getJSONObject(i);
                            Holiday holiday = new Holiday();
                            holiday.date = holidayJson.getString("Day");
                            holiday.month = holidayJson.getString("Month");
                            holiday.day = holidayJson.getString("DayName");
                            holiday.mmddyy = holidayJson.getString("Date");
                            holiday.description = holidayJson.getString("Description");

                            if (holidayJson.getString("Type").equalsIgnoreCase("O")) {
                                holiday.isOptional = true;
                            } else {
                                holiday.isOptional = false;
                            }
                            holidays.add(holiday);
                        }
                    }
                }
            } catch (JSONException je) {

            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if (holidays != null && holidays.size() > 0) {
                holidayGridAdapter.setHolidayList(holidays);
                holidayGridAdapter.notifyDataSetChanged();
                gridView_holidays.setVisibility(View.VISIBLE);
                tvNoHolidays.setVisibility(View.GONE);
            } else {
                gridView_holidays.setVisibility(View.INVISIBLE);
                tvNoHolidays.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(s);
        }
    }
}
