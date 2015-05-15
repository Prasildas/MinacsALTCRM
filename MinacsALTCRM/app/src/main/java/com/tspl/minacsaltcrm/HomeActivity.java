package com.tspl.minacsaltcrm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends BaseActivity implements OnClickListener {

    Context mContext;
    Button button_messages, button_holidays, button_queries, button_survey,
            button_feedback, button_map;
    Button button_contact;
    boolean isRunning = false;
    private SharedPreferences preferences;
    TextView textView_UnReadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initView();

        clickListners();
    }

    /**
     * initialise
     */
    private void init() {
        mContext = HomeActivity.this;
        preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
    }
    /**
     * initialise view
     */
    private void initView() {
        setContentView(R.layout.activity_home);
        button_messages = (Button) findViewById(R.id.button_messages);
        button_holidays = (Button) findViewById(R.id.button_holidays);
        button_queries = (Button) findViewById(R.id.button_queries);
        button_survey = (Button) findViewById(R.id.button_survey);
        button_feedback = (Button) findViewById(R.id.button_feedback);
        button_map = (Button) findViewById(R.id.button_map);
        button_contact = (Button) findViewById(R.id.button_contact);

        textView_UnReadCount = (TextView) findViewById(R.id.textView_UnReadCount);
        textView_UnReadCount.setVisibility(View.INVISIBLE);

		/*Footer buttons*/
        button_home = (Button) findViewById(R.id.button_home);
        button_myProfile = (Button) findViewById(R.id.button_myProfile);
        button_social = (Button) findViewById(R.id.button_social);
        button_contact = (Button) findViewById(R.id.button_contact);

		/*For Home, Disabling home button*/
        super.bindFooterViews(mContext);
        button_home.setEnabled(false);

    }
    /**
     * binding click events
     */
    private void clickListners() {
        button_messages.setOnClickListener(this);
        button_holidays.setOnClickListener(this);
        button_queries.setOnClickListener(this);
        button_survey.setOnClickListener(this);
        button_feedback.setOnClickListener(this);
        button_map.setOnClickListener(this);
        button_home.setOnClickListener(this);
        button_myProfile.setOnClickListener(this);
        button_social.setOnClickListener(this);
        button_contact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.button_messages:

                intent = new Intent(mContext, MessageActivity.class);
                startActivity(intent);

                break;

            case R.id.button_holidays:
                intent = new Intent(mContext, HolidaysActivity.class);
                startActivity(intent);
                break;

            case R.id.button_queries:
                intent = new Intent(mContext, QueriesActivity.class);
                startActivity(intent);
                break;

            case R.id.button_survey:

                intent = new Intent(mContext, SurveyActivity.class);
                startActivity(intent);

                break;

            case R.id.button_feedback:
                intent = new Intent(mContext, FeedbackActivity.class);
                startActivity(intent);
                break;

            case R.id.button_map:
                intent = new Intent(mContext, MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.button_myProfile:
                intent = new Intent(mContext, ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.button_social:
                intent = new Intent(mContext, SocialActivity.class);
                startActivity(intent);
                break;

            case R.id.button_contact:
                intent = new Intent(mContext, ContactActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundAsync backgroundAsync = new BackgroundAsync(preferences.getString(AppConstants.employeeID, ""));
        backgroundAsync.execute();
    }

    /**
     * BackgroundAsync for fetching the unread message count
     */
    public class BackgroundAsync extends AsyncTask<Void, Void, Integer> {
        private String employeeID;

        public BackgroundAsync(String _employeeID) {
            employeeID = _employeeID;
        }

        @Override
        protected void onPreExecute() {
            isRunning = true;
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().getUnreadMessageCount(employeeID);
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("Count")) {
                    return jsonObject.getInt("Count");
                }
            } catch (JSONException je) {

            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer s) {
            isRunning = false;
            Pr.ln("Count --" + s);
            if (s >= 0) {
                textView_UnReadCount.setVisibility(View.VISIBLE);
                textView_UnReadCount.setText("" + s);
            } else {
                textView_UnReadCount.setVisibility(View.INVISIBLE);
            }
            super.onPostExecute(s);

        }

    }
}
