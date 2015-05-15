package com.tspl.minacsaltcrm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedbackActivity extends BaseActivity {
    Context mContext;
    private SharedPreferences preferences;
    Toast mToast;
    EditText editText_subject, editText_content;
    Button button_update, button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initData();
        initViews();
        super.bindFooterViews(mContext);
    }

    /**
     * initialise
     */
    private void init() {
        mContext = FeedbackActivity.this;
        preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
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
        setContentView(R.layout.feedback);
        button_update = (Button) findViewById(R.id.button_update);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        final EditText editText_subject = (EditText) findViewById(R.id.editText_subject);
        final EditText editText_content = (EditText) findViewById(R.id.editText_content);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundAsync backgroundAsync = new BackgroundAsync(preferences.getString("employeeID", ""), editText_subject.getText().toString().trim(), editText_content.getText().toString().trim());
                backgroundAsync.execute();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Updating feedback
     */
    public class BackgroundAsync extends AsyncTask<Void, Void, String> {
        private String employeeID, subject, content;
        private int pageNo;

        public BackgroundAsync(String _employeeID, String _subject, String _content) {
            employeeID = _employeeID;
            subject = _subject;
            content = _content;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().updateFeedback(employeeID, subject, content);
            } catch (JSONException je) {

            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.has("Acknowledge") && jsonObject.getInt("Acknowledge") == 1) {
                    mToast.setText("Feedback Submitted");
                    mToast.show();
                    finish();
                }
            } catch (JSONException je) {

            }
            super.onPostExecute(s);

        }
    }
}
