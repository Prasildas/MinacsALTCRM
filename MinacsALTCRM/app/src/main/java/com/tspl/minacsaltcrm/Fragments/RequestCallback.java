package com.tspl.minacsaltcrm.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tspl.minacsaltcrm.AppConstants;
import com.tspl.minacsaltcrm.QueriesActivity;
import com.tspl.minacsaltcrm.R;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t0396 on 5/6/2015.
 */
public class RequestCallback extends Fragment {
    Context context;
    Activity pActivity;
    View rootView;
    Toast toast;
    SharedPreferences preferences;
    private Button btnApply, btnCancel;
    private EditText etName, etphone, etEmail, etComment;
    Spinner sp_countryCode;
    private String employeeID;

    public RequestCallback() {

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
        rootView = inflater.inflate(R.layout.requestcallback, container,
                false);

        initViews(rootView);

        loadData();
        clickListners();
        return rootView;
    }
    /*
        initialise views
         */
    private void initViews(View rootView) {
        preferences = getActivity().getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, Context.MODE_PRIVATE);
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        employeeID = preferences.getString(AppConstants.employeeID, "");
        btnApply = (Button) rootView.findViewById(R.id.btnApply);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        etName = (EditText) rootView.findViewById(R.id.etName);
        etphone = (EditText) rootView.findViewById(R.id.etphone);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etComment = (EditText) rootView.findViewById(R.id.etComment);
        sp_countryCode = (Spinner) rootView.findViewById(R.id.sp_countryCode);
    }

    /**
     * load initial data
     */
    private void loadData() {

        FetchBackgroundAsync fetchBackgroundAsync = new FetchBackgroundAsync();
        fetchBackgroundAsync.execute();
    }
    /**
     * binding click events
     */
    private void clickListners() {
        btnApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestCallback();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goBack();
            }


        });
    }

    /**
     * go back to show previous fragment
     */
    private void goBack() {
        ((QueriesActivity) getActivity()).goBack();
    }
    /**
     * request call back sending to server
     */
    private void requestCallback() {
        RequestCallbackBackgroundAsync requestCallbackBackgroundAsync = new RequestCallbackBackgroundAsync(preferences.getString(AppConstants.employeeID, ""));
        requestCallbackBackgroundAsync.execute();
    }
    /**
     * Fetching details(profile) for showing mail, phone, etc
     */
    public class FetchBackgroundAsync extends AsyncTask<Void, Void, String> {

        ProgressDialog pd;
        List<String> countryCodesList;

        public FetchBackgroundAsync() {
            countryCodesList = new ArrayList<String>();
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
                result = new SoapHandler().getProfile(employeeID);
                String countryCodes = new SoapHandler().getCountryCode();
                JSONObject countryCodesJson = new JSONObject(countryCodes);
                if (countryCodesJson.has("Acknowledge") && countryCodesJson.getInt("Acknowledge") == 1) {
                    if (countryCodesJson.has("AttributeValues")) {
                        JSONArray attributeValues = countryCodesJson.getJSONArray("AttributeValues");
                        for (int i = 0; i < attributeValues.length(); i++) {
                            JSONObject json = attributeValues.getJSONObject(i);
                            if (json.has("Value") && !json.isNull("Value")) {
                                countryCodesList.add(json.getString("Value"));
                            } else {
                                countryCodesList.add(" ");
                            }
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
            if (s.length() > 0) {
                if (countryCodesList.size() > 0) {
                    ArrayAdapter adapter = new ArrayAdapter<String>(context,
                            R.layout.cust_spinner_1, countryCodesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_countryCode.setAdapter(adapter);
                }
                try {
                    JSONObject resp = new JSONObject(s);
                    if (resp.has("Mobile")) {
                        String phone = resp.getString("Mobile");
                        if (phone == null) {
                            phone = "";
                        }
                        etphone.setText(phone);
                    }

                    if (resp.has("OfficeEmail")) {
                        String officeEmail = resp.getString("OfficeEmail");
                        if (officeEmail == null) {
                            officeEmail = "";
                        }
                        etEmail.setText(officeEmail);
                    }
                    if (resp.has("UserName")) {
                        String userName = resp.getString("UserName");
                        if (userName == null) {
                            userName = "";
                        }
                        etName.setText(userName);
                    }

                } catch (JSONException je) {
                    toast.setText("Failed to fetch and parse data");
                    toast.show();
                }
            } else {
                toast.setText("Failed to fetch data");
                toast.show();
            }
            super.onPostExecute(s);
        }
    }

    public class RequestCallbackBackgroundAsync extends AsyncTask<Void, Void, String> {
        private String employeeID;
        ProgressDialog pd;

        public RequestCallbackBackgroundAsync(String _employeeID) {
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

                JSONObject json = new JSONObject();
                json.put("Comments", etComment.getText().toString().trim());
                json.put("ContactNumber", sp_countryCode.getSelectedItem().toString().trim() + etphone.getText().toString().trim());
                json.put("CustomerName", etName.getText().toString().trim());
                json.put("EmailAddress", etEmail.getText().toString().trim());
                json.put("EmployeeId", etEmail.getText().toString().trim());
                json.put("FirstName", null);
                json.put("LastName", null);
                json.put("Active", false);
                json.put("CreatedBy", null);
                json.put("CreatedOn", null);
                json.put("ModifiedBy", null);
                json.put("ModifiedOn", null);
                json.put("ValidationErrors", null);
                result = new SoapHandler().requestCallback(json);
                JSONObject respObj = new JSONObject(result);
                if (respObj.has("Acknowledge") && respObj.getInt("Acknowledge") == 1) {
                    if (respObj.has("Message")) {
                        return respObj.getString("Message");
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
                toast.setText("Failed to submit requested leave.");
                toast.show();
            }
            super.onPostExecute(s);
        }
    }


}
