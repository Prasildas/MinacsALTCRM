package com.tspl.minacsaltcrm;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private EditText editText_name, editText_email_personal, editText_email_official, editText_countryCode, editText_phone, editText_FB, editText_Twitter;
    private Button button_cancel, button_update;
    private SharedPreferences preferences;
    protected Button button_home, button_myProfile, button_social, button_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initData();
        initViews();
        clickListners();
        super.bindFooterViews(mContext);
        button_myProfile.setEnabled(false);
        BackgroundAsync backgroundAsync = new BackgroundAsync(preferences.getString("employeeID", ""));
        backgroundAsync.execute();
    }

    /**
     * Initialise
     */
    private void init() {
        mContext = ProfileActivity.this;
        preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
    }
    /**
     * Initialise data
     */
    private void initData() {
    }
    /**
     * Initialise view
     */
    private void initViews() {
        setContentView(R.layout.profile2);
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_email_personal = (EditText) findViewById(R.id.editText_email_personal);
        editText_email_official = (EditText) findViewById(R.id.editText_email_official);
        editText_countryCode = (EditText) findViewById(R.id.editText_countryCode);
        editText_phone = (EditText) findViewById(R.id.editText_phone);
        editText_FB = (EditText) findViewById(R.id.editText_FB);
        editText_Twitter = (EditText) findViewById(R.id.editText_Twitter);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_update = (Button) findViewById(R.id.button_update);

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
    /**
     * bind click events
     */
    private void clickListners() {
        button_cancel.setOnClickListener(this);
        button_update.setOnClickListener(this);
    }

    Intent intent;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_update:
                BackgroundAsync backgroundAsync = new BackgroundAsync(preferences.getString("employeeID", ""), true);
                backgroundAsync.execute();
                break;

            case R.id.button_cancel:
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

    /**
     * Update profile changes/ fetching profile details
     * isGetRq = true for fetching data, else updating data
     */
    public class BackgroundAsync extends AsyncTask<Void, Void, String> {
        private String employeeID;
        boolean isGetRq = false;
        ProgressDialog pd;
        String phone, twitter, facebook, personalEmail, officialEmail, userName;

        public BackgroundAsync(String _employeeID) {
            employeeID = _employeeID;
            isGetRq = true;
        }

        public BackgroundAsync(String _employeeID, String phone, String twitter, String facebook, String personalEmail, String officialEmail) {
            employeeID = _employeeID;
            isGetRq = false;
            this.phone = phone;
            this.twitter = twitter;
            this.facebook = facebook;
            this.personalEmail = personalEmail;
            this.officialEmail = officialEmail;
        }

        public BackgroundAsync(String _employeeID, boolean isUpdate) {
            //UPDATE CALL
            employeeID = _employeeID;
            isGetRq = false;
            String getPhoneNum = editText_phone.getText().toString().trim();
            String getTwitter = editText_Twitter.getText().toString().trim();
            String getFb = editText_FB.getText().toString().trim();
            String getOffEmail = editText_email_official.getText().toString().trim();
            String getPersEmail = editText_email_personal.getText().toString().trim();
            String getUserName = editText_name.getText().toString().trim();


            if (getPhoneNum == "" || getPhoneNum == null) {
                this.phone = "";
            } else {
                this.phone = getPhoneNum;
            }
            if (getTwitter == "" || getTwitter == null) {
                this.twitter = "";
            } else {
                this.twitter = getTwitter;
            }
            if (getFb == "" || getFb == null) {
                this.facebook = "";
            } else {
                this.facebook = getFb;
            }
            if (getOffEmail == "" || getOffEmail == null) {
                this.officialEmail = "";
            } else {
                this.officialEmail = getOffEmail;
            }
            if (getPersEmail == "" || getPersEmail == null) {
                this.personalEmail = "";
            } else {
                this.personalEmail = getPersEmail;
            }
            if (getUserName == "" || getUserName == null) {
                this.userName = "";
            } else {
                this.userName = getUserName;
            }
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
                if (isGetRq) {
                    result = new SoapHandler().getProfile(employeeID);
                } else {
                    result = new SoapHandler().updateMyProfile(employeeID, phone, twitter, facebook, personalEmail, officialEmail, userName);
                }
            } catch (JSONException je) {

            }
            Pr.ln("GOT RESULT");
            Pr.ln(result.toString());
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
//        Toast.makeText(mContext,s,Toast.LENGTH_LONG).show();
            if (isGetRq) {
                try {
                    JSONObject resp = new JSONObject(s);
                    if (resp.has("Mobile")) {
                        String phone = resp.getString("Mobile");
                        if (phone == null) {
                            phone = "";
                        }
                        editText_phone.setText(phone);
                    }
                    if (resp.has("TwitterId")) {
                        String twitter = resp.getString("TwitterId");
                        if (twitter == null) {
                            twitter = "";
                        }
                        editText_Twitter.setText(twitter);
                    }
                    if (resp.has("FacebookId")) {
                        String facebook = resp.getString("FacebookId");
                        if (facebook == null) {
                            facebook = "";
                        }
                        editText_FB.setText(facebook);
                    }
                    if (resp.has("OfficeEmail")) {
                        String offEmail = resp.getString("OfficeEmail");
                        if (offEmail == null) {
                            offEmail = "";
                        }
                        editText_email_official.setText(offEmail);
                    }
                    if (resp.has("PersonalEmail")) {
                        String perEmail = resp.getString("PersonalEmail");
                        if (perEmail == null) {
                            perEmail = "";
                        }
                        editText_email_personal.setText(perEmail);
                    }
                    if (resp.has("UserName")) {
                        String userName = resp.getString("UserName");
                        if (userName == null) {
                            userName = "";
                        }
                        editText_name.setText(userName);
                    }
                } catch (JSONException je) {
                    finish();
                }
            } else {
                finish();
            }
            super.onPostExecute(s);
        }
    }

}
