package com.tspl.minacsaltcrm.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.tspl.minacsaltcrm.AppConstants;
import com.tspl.minacsaltcrm.ClassObject.Items;
import com.tspl.minacsaltcrm.MapsActivity;
import com.tspl.minacsaltcrm.R;
import com.tspl.minacsaltcrm.adapters.QueriesListAdapter;
import com.tspl.minacsaltcrm.views.CirclularDivider;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t0396 on 5/6/2015.
 */
public class RequestFragments extends Fragment {
    Context context;
    Activity pActivity;
    View rootView;
    ListView listView_queries;
    Toast toast;
    private SharedPreferences preferences;

    public RequestFragments() {

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        pActivity = activity;
        context = getActivity();
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        rootView = inflater.inflate(R.layout.queries_new, container,
                false);
        preferences = context.getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, Context.MODE_PRIVATE);
        initViews(rootView);
//        loadListeners();
        loadData();
        return rootView;
    }

    /*
    initialise views
     */
    List<Items> items;
    private void initViews(View rootView) {

       /* listView_queries = (ListView) rootView.findViewById(R.id.listView_queries);
        listView_queries.setAdapter(new QueriesListAdapter(context));*/

        CirclularDivider view = (CirclularDivider)rootView.findViewById(R.id.circularView);
        items = new ArrayList<Items>();
        Items item1 = new Items("Change my_Optional Holiday", getResources().getColor(R.color.minacs_green));
        Items item2 = new Items("Request for_Leave", getResources().getColor(R.color.minacs_green));
        Items item3 = new Items("Request for_Callback", getResources().getColor(R.color.minacs_green));
        Items item4 = new Items("Request for_Policy", getResources().getColor(R.color.minacs_green));
        Items item5 = new Items("Schedule an_Appointment", getResources().getColor(R.color.minacs_green));
        Items item6 = new Items("PF Enquiry", getResources().getColor(R.color.minacs_green));
        Items item7 = new Items("Leave balance", getResources().getColor(R.color.minacs_green));
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);
        view.setData(items);
        view.setCenterCircleColor(R.color.bg_screen);
        view.setClickListener(new CirclularDivider.Click() {
            @Override
            public void onClick(int position) {
                /*String item =  items.get(position).label;
                toast.setText(item + " selected");
                toast.show();*/
                loadClickedItems(position);
            }
        });

    }

    /**
     * Click listner
     */
    private void loadListeners() {
        listView_queries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadClickedItems(position);
            }
        });
    }
    private void loadClickedItems(int position) {
        switch (position) {
            case 0:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ChangeMyOptionalHoliday()).addToBackStack(null).commit();
                break;

            case 1:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new RequestLeave()).addToBackStack(null).commit();
                break;

            case 2:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new RequestCallback()).addToBackStack(null).commit();
                break;

            case 3:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new RequestPolicy()).addToBackStack(null).commit();
                break;

            case 4:
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                break;

            case 5:// PF ENQUIRY
                BackgroundAsync backgroundAsync = new BackgroundAsync(preferences.getString(AppConstants.employeeID, ""), 1);
                backgroundAsync.execute();
                break;

            case 6:
//                        Leave balance
                BackgroundAsync backgroundAsync1 = new BackgroundAsync(preferences.getString(AppConstants.employeeID, ""), 0);
                backgroundAsync1.execute();
                break;


            default:
                Intent intent1 = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent1);
                break;
        }
    }
    private void loadData() {
    }

    /**
     * PF Enquiry and Leave balance enquiry
     * queryType = 1 for PF,
     * queryType = 0 for leave
     */
    public class BackgroundAsync extends AsyncTask<Void, Void, String> {
        private String employeeID;
        private int queryType;
        ProgressDialog pd;
        Toast mToast;

        public void setQueryType(int queryType) {
            this.queryType = queryType;
        }

        public BackgroundAsync(String _employeeID, int _queryType) {
            employeeID = _employeeID;
            queryType = _queryType;
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
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
                result = new SoapHandler().getQuery(employeeID, queryType);
            } catch (JSONException je) {

            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.has("Acknowledge") && jsonObject.getInt("Acknowledge") == 1) {
                    if (queryType == 1) {
//                        PF
                        showResultMessage("Your PF credit details has been sent. Please check your inbox", "PF Enquiry");
                    } else {
                        showResultMessage("Your Leave balance request has been sent. Please check your inbox", "Leave Balance Enquiry");
                    }

                } else {
                    mToast.setText("Error occurred. Please try again");
                    mToast.show();
                }
            } catch (JSONException je) {

            }
            super.onPostExecute(s);

        }

    }

    public void showResultMessage(final String msg, final String heading) {
        new AlertDialog.Builder(context)
                .setTitle(heading)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
