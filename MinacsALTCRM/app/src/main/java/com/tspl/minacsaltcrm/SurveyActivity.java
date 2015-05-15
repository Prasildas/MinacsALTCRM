package com.tspl.minacsaltcrm;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tspl.minacsaltcrm.ClassObject.Ratings;
import com.tspl.minacsaltcrm.ClassObject.SurveyItem;
import com.tspl.minacsaltcrm.adapters.SurveyAdapter;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SurveyActivity extends BaseActivity implements View.OnClickListener {
    Toast mToast;
    ListView listView;
    SurveyAdapter surveyAdapter;
    Context mContext;
    List<SurveyItem> surveys;
    private SharedPreferences preferences;
    String employeeID;
    Button button_cancel, button_update;
    protected Button button_home, button_myProfile, button_social, button_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initViews();
        clickListners();


        BackgroundAsync backgroundAsync = new BackgroundAsync(employeeID);
        backgroundAsync.execute();
    }

    private void init() {
        surveyAdapter = new SurveyAdapter(this);
        mContext = SurveyActivity.this;
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
        surveys = new ArrayList<SurveyItem>();
        employeeID = preferences.getString(AppConstants.employeeID, "");
    }

    private void initViews() {
        setContentView(R.layout.activity_survey);
        button_cancel = (Button) findViewById(R.id.button_SurveyCancel);
        button_update = (Button) findViewById(R.id.button_SurveyUpdate);
        listView = (ListView) findViewById(R.id.listView_main);

        listView.setAdapter(surveyAdapter);
        super.bindFooterViews(SurveyActivity.this);

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

    }

    private void clickListners() {
        button_cancel.setOnClickListener(this);
        button_update.setOnClickListener(this);
    }

    Intent intent;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_SurveyUpdate:
                UpdateSurveyBackgroundAsync updateSurvey = new UpdateSurveyBackgroundAsync(employeeID, surveyAdapter.getRatingsList());
                updateSurvey.execute();
                break;

            case R.id.button_SurveyCancel:
                finish();
                break;

            case R.id.button_myProfile:
                intent = new Intent(mContext, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.button_social:
                intent = new Intent(mContext, SocialActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.button_home:
                Intent intentHome = new Intent(mContext, HomeActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                finish();
                break;

            case R.id.button_contact:
                intent = new Intent(mContext, ContactActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    public class BackgroundAsync extends AsyncTask<Void, Void, Integer> {
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
        protected Integer doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().getSurvey(employeeID);
                Pr.ln(result);
                JSONObject surveyObj = new JSONObject(result);
                if (surveyObj.has("Acknowledge") && surveyObj.getInt("Acknowledge") == 1) {
                    if (surveyObj.has("Survey")) {
                        JSONArray surveyJsonArr = surveyObj.getJSONArray("Survey");
                        for (int i = 0; i < surveyJsonArr.length(); i++) {
                            JSONObject surveyJson = surveyJsonArr.getJSONObject(i);
                            SurveyItem item = new SurveyItem();
                            item.question = surveyJson.getString("Question");
                            if (surveyJson.has("QuestionId"))
                                item.id = surveyJson.getInt("QuestionId");
                            item.rating = surveyJson.getInt("Rating");
                            surveys.add(item);
                        }
                        return surveyJsonArr.length();
                    }
                }
            } catch (JSONException je) {

            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer i) {
            pd.dismiss();
            surveyAdapter.setSurveyList(surveys);
            surveyAdapter.notifyDataSetChanged();
            super.onPostExecute(i);
        }
    }

    public class UpdateSurveyBackgroundAsync extends AsyncTask<Void, Void, Boolean> {
        private String employeeID;
        private long messageId;
        List<Ratings> listDatas;

        public UpdateSurveyBackgroundAsync(String _employeeID, List<Ratings> list) {
            employeeID = _employeeID;
            listDatas = list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().updateSurvey(employeeID, listDatas);
                Pr.ln(result);
                JSONObject messageObj = new JSONObject(result);
                if (messageObj.has("Acknowledge") && messageObj.getInt("Acknowledge") == 1) {
                    return true;
                }
            } catch (JSONException je) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean i) {
            if (i) {
                mToast.setText("Survey has been updated successfully.");
                finish();

            } else {
                mToast.setText("Failed to update the survey.");
            }
            mToast.show();
            super.onPostExecute(i);
        }
    }

}
