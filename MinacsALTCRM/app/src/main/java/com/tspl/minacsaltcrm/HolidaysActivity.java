package com.tspl.minacsaltcrm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.tspl.minacsaltcrm.ClassObject.Holiday;
import com.tspl.minacsaltcrm.adapters.HolidayGridAdapter;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HolidaysActivity extends FragmentActivity implements View.OnClickListener {
    Context mContext;
    private TextView tvNoHolidays;
    private GridView gridView_holidays;

    protected Button button_home, button_myProfile, button_social, button_contact;
    List<Holiday> holidays;
    HolidayGridAdapter holidayGridAdapter;
    private SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        mContext = this;
        preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
        holidays = new ArrayList<Holiday>();
        setContentView(R.layout.holidays);


        gridView_holidays = (GridView) findViewById(R.id.gridView_holidays);
        holidayGridAdapter = new HolidayGridAdapter(mContext);
        gridView_holidays.setAdapter(holidayGridAdapter);
        tvNoHolidays = (TextView) findViewById(R.id.tvNoHolidays);

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

        BackgroundAsync backgroundAsync = new BackgroundAsync(preferences.getString(AppConstants.employeeID, ""));
        backgroundAsync.execute();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.button_myProfile:
                intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.button_social:
                intent = new Intent(this, SocialActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.button_home:
                Intent intentHome = new Intent(this, HomeActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                finish();
                break;

            case R.id.button_contact:
                intent = new Intent(this, ContactActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    /**
     * BackgroundAsync fetches the list of holidays
     */
    public class BackgroundAsync extends AsyncTask<Void, Void, String> {
        private String employeeID;
        ProgressDialog pd;

        public BackgroundAsync(String _employeeID) {
            employeeID = _employeeID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
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
