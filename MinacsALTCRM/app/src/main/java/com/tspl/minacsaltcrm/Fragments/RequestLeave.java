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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tspl.minacsaltcrm.AppConstants;
import com.tspl.minacsaltcrm.ClassObject.Holiday;
import com.tspl.minacsaltcrm.ClassObject.LeaveTypes;
import com.tspl.minacsaltcrm.Popup.ChangeMyOptionalHolidayPopup;
import com.tspl.minacsaltcrm.QueriesActivity;
import com.tspl.minacsaltcrm.R;
import com.tspl.minacsaltcrm.adapters.ChangeMyHolidayGridAdapter;
import com.tspl.minacsaltcrm.adapters.LeaveTypesSpinner;
import com.tspl.minacsaltcrm.views.DatePickerFragment;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by t0396 on 5/6/2015.
 */
public class RequestLeave extends Fragment {
    Context context;
    Activity pActivity;
    View rootView;
    Toast toast;
    TextView tvNoHolidays;
    SharedPreferences preferences;
    private GridView gridView_holidays;
    ChangeMyOptionalHolidayPopup changeMyOptionalHolidayPopup;

    List<Holiday> holidays;
    ChangeMyHolidayGridAdapter holidayGridAdapter;
    private Button btnApply, btnCancel;
    public EditText dateFrom, dateTo, etReason;
    Spinner spinner_LeaveType;

    private EditText etComments;

    public RequestLeave() {

    }

    @Override
    public void onAttach(Activity activity) {
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
        rootView = inflater.inflate(R.layout.requestleave, container,
                false);

        initViews(rootView);

        loadData();
        clickListners();
        return rootView;
    }

    LeaveTypesSpinner leavetypesAdapter;

    /**
     * Initialise views
     * @param rootView
     */
    private void initViews(View rootView) {
        preferences = getActivity().getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, Context.MODE_PRIVATE);
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        leavetypesAdapter = new LeaveTypesSpinner(context);
        btnApply = (Button) rootView.findViewById(R.id.btnApply);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        dateTo = (EditText) rootView.findViewById(R.id.dateTo);
        dateFrom = (EditText) rootView.findViewById(R.id.dateFrom);
        etReason = (EditText) rootView.findViewById(R.id.etComments);
        spinner_LeaveType = (Spinner) rootView.findViewById(R.id.spinner_LeaveType);
        spinner_LeaveType.setAdapter(leavetypesAdapter);
    }

    /**
     * initialise data, fetching from server
     */
    private void loadData() {

        FetchLeaveTypesBackgroundAsync fetchLeaveTypesBackgroundAsync = new FetchLeaveTypesBackgroundAsync();
        fetchLeaveTypesBackgroundAsync.execute();
    }

    /**
     * bind click events
     */
    private void clickListners() {


        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateFromDialog();

            }
        });
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateToDialog();

            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestLeave();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((QueriesActivity) getActivity()).goBack();
            }
        });
    }

    /**
     * request leave, submit
     */
    private void requestLeave() {
        int selectedPos = spinner_LeaveType.getSelectedItemPosition();
        LeaveTypes leaveTypes = leavetypesAdapter.getItem(selectedPos);
        if (dateFrom.getText().toString().trim().length() > 0 && dateTo.getText().toString().trim().length() > 0 && compireDates(dateFrom.getText().toString().trim(), dateTo.getText().toString().trim())) {
            RequestLeaveBackgroundAsync requestLeaveBackgroundAsync = new RequestLeaveBackgroundAsync(preferences.getString(AppConstants.employeeID, ""), leaveTypes.attributeValueId);
            requestLeaveBackgroundAsync.execute();
        } else {
            toast.setText("Error in selected dates.");
            toast.show();
        }
    }

    List<LeaveTypes> leaveTypesList;

    public class FetchLeaveTypesBackgroundAsync extends AsyncTask<Void, Void, String> {
        private String employeeID;
        ProgressDialog pd;

        public FetchLeaveTypesBackgroundAsync() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setMessage("Please wait...");
            pd.show();
            leaveTypesList = new ArrayList<LeaveTypes>();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().getLeaveTypes();
                JSONObject holidaysObj = new JSONObject(result);
                if (holidaysObj.has("Acknowledge") && holidaysObj.getInt("Acknowledge") == 1) {
                    if (holidaysObj.has("AttributeValues")) {
                        JSONArray leaveTypesJsonArr = holidaysObj.getJSONArray("AttributeValues");
                        for (int i = 0; i < leaveTypesJsonArr.length(); i++) {
                            JSONObject json = leaveTypesJsonArr.getJSONObject(i);
                            LeaveTypes leaveTypes = new LeaveTypes();
                            leaveTypes.attributeValueId = json.getInt("AttributeValueId");
                            leaveTypes.name = json.getString("Name");
                            leaveTypes.value = json.getString("Value");
                            leaveTypesList.add(leaveTypes);
                        }
                        leavetypesAdapter.setLeaveTypes(leaveTypesList);
                    }
                }
            } catch (JSONException je) {

            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if (leaveTypesList != null && leaveTypesList.size() > 0) {
                leavetypesAdapter.notifyDataSetChanged();
            } else {

            }
            super.onPostExecute(s);
        }
    }

    public class RequestLeaveBackgroundAsync extends AsyncTask<Void, Void, String> {
        private String employeeID;
        ProgressDialog pd;
        int attributeValueId;

        public RequestLeaveBackgroundAsync(String _employeeID, int attributeValueId) {
            employeeID = _employeeID;
            this.attributeValueId = attributeValueId;
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
                result = new SoapHandler().requestLeave(employeeID, dateFrom.getText().toString(), dateTo.getText().toString(), attributeValueId, etReason.getText().toString().trim());
                JSONObject holidaysObj = new JSONObject(result);
                if (holidaysObj.has("Acknowledge") && holidaysObj.getInt("Acknowledge") == 1) {
                    if (holidaysObj.has("Message")) {
                        return holidaysObj.getString("Message");
                    }
                }
            } catch (JSONException je) {

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if (s.length() > 0) {
                toast.setText(s);
                toast.show();
                ((QueriesActivity) getActivity()).goBack();
            } else {

            }
            super.onPostExecute(s);
        }
    }

    /**
     * Date from fragment
     * @return
     */
    public String showDateFromDialog() {
        final String selectedDate[] = new String[1];
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(fm, "date");
        datePickerFragment.setDialogDataConnector(new DatePickerFragment.UpdateFromDateDialog() {
            @Override
            public void setDate(String[] date) {
                selectedDate[0] = date[0];
                dateFrom.setText(selectedDate[0]);
            }
        });
        return selectedDate[0];
    }
    /**
     * Date to fragment
     * @return
     */
    public String showDateToDialog() {
        final String selectedDate[] = new String[1];
        android.app.FragmentManager fm = ((Activity) context).getFragmentManager();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(fm, "date");
        datePickerFragment.setDialogDataConnector(new DatePickerFragment.UpdateFromDateDialog() {
            @Override
            public void setDate(String[] date) {
                selectedDate[0] = date[0];
                if (dateFrom.getText().toString().trim().length() > 0 && compireDates(dateFrom.getText().toString().trim(), selectedDate[0]))
                    dateTo.setText(selectedDate[0]);
            }
        });
        return selectedDate[0];
    }

    /**
     * compiring dates to validate from & to dates
     * @param d1
     * @param d2
     * @return
     */
    public boolean compireDates(String d1, String d2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            if (date1.compareTo(date2) > 0) {
                System.out.println("Date1 is after Date2");
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
