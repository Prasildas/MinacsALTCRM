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
import android.widget.Spinner;
import android.widget.Toast;

import com.tspl.minacsaltcrm.AppConstants;
import com.tspl.minacsaltcrm.ClassObject.LeaveTypes;
import com.tspl.minacsaltcrm.QueriesActivity;
import com.tspl.minacsaltcrm.R;
import com.tspl.minacsaltcrm.adapters.PolicyTypesSpinnerAdapter;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t0396 on 5/6/2015.
 */
public class RequestPolicy extends Fragment {
    Context context;
    Activity pActivity;
    View rootView;
    Toast toast;
    SharedPreferences preferences;
    private Button btnApply, btnCancel;
    private EditText etComment;
    private String employeeID;
    Spinner spinnerPolicy;
    PolicyTypesSpinnerAdapter adapter;

    public RequestPolicy() {

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
        rootView = inflater.inflate(R.layout.requestpolicy, container,
                false);

        initViews(rootView);

        loadData();
        clickListners();
        return rootView;
    }

    /**
     * Initialise views
     * @param rootView
     */
    private void initViews(View rootView) {
        preferences = getActivity().getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, Context.MODE_PRIVATE);
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        employeeID = preferences.getString(AppConstants.employeeID, "");
        adapter = new PolicyTypesSpinnerAdapter(context);

        btnApply = (Button) rootView.findViewById(R.id.btnApply);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        spinnerPolicy = (Spinner) rootView.findViewById(R.id.spinnerPolicy);
        etComment = (EditText) rootView.findViewById(R.id.etComment);
        spinnerPolicy.setAdapter(adapter);
    }

    /**
     * Fetching data
     */
    private void loadData() {

        FetchBackgroundAsync fetchBackgroundAsync = new FetchBackgroundAsync();
        fetchBackgroundAsync.execute();
    }

    /**
     * bind click events
     */
    private void clickListners() {
        btnApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestPolicy();
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
     * Request for selected policy
     */
    private void requestPolicy() {

        RequestPolicykBackgroundAsync requestPolicykBackgroundAsync = new RequestPolicykBackgroundAsync();
        requestPolicykBackgroundAsync.execute();
    }

    public class FetchBackgroundAsync extends AsyncTask<Void, Void, String> {
        List<LeaveTypes> leaveTypesList = new ArrayList<LeaveTypes>();
        ProgressDialog pd;

        public FetchBackgroundAsync() {

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
                result = new SoapHandler().getPolicyList();
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
                        adapter.setLeaveTypes(leaveTypesList);
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
                adapter.notifyDataSetChanged();
            } else {
                toast.setText("Failed to fetch data");
                toast.show();
            }
            super.onPostExecute(s);
        }
    }

    public class RequestPolicykBackgroundAsync extends AsyncTask<Void, Void, String> {
        ProgressDialog pd;

        public RequestPolicykBackgroundAsync() {

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
                int position = spinnerPolicy.getSelectedItemPosition();
                LeaveTypes type = adapter.getItem(position);
                result = new SoapHandler().requestPolicy(employeeID, etComment.getText().toString().trim(), type.attributeValueId + "");
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
