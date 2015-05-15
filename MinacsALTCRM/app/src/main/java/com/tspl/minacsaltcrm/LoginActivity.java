package com.tspl.minacsaltcrm;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity {
    Context mContext;
    EditText editText_username, editText_password;
    Button button_login;
    CheckBox cb_rememberMe;
    private SharedPreferences preferences;
    Toast mToast;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initData();
        initViews();
        initListners();
        clickListners();
    }

    private void clickListners() {
        button_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BackgroundAsync backgroundAsync = new BackgroundAsync(editText_username.getText().toString().trim(), editText_password.getText().toString().trim());
                backgroundAsync.execute();
            }
        });
    }

    private void init() {
        mContext = LoginActivity.this;
        preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        pd = new ProgressDialog(mContext);
    }

    private void initData() {
    }

    private void initViews() {
        setContentView(R.layout.login);
        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_password = (EditText) findViewById(R.id.editText_password);
        button_login = (Button) findViewById(R.id.button_login);
        cb_rememberMe = (CheckBox) findViewById(R.id.cb_rememberMe);
        /*if(D) {
            editText_username.setText("usha.vantepuli");
            editText_password.setText("aprMinacs123");
//            editText_username.setText("anilkumar.vl");
//            editText_password.setText("Minacs098");
            button_login.setEnabled(true);
        }*/

        if(preferences.getString("userName", "").length() > 0) {
            editText_username.setText(preferences.getString("userName", ""));
        }
        if(preferences.getString("password", "").length() > 0) {
            editText_password.setText(preferences.getString("password", ""));
            cb_rememberMe.setChecked(true);
            button_login.setEnabled(true);
        }

    }

    private void initListners() {
        editText_username.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && editText_password.getText().toString().trim().length() > 0) {
                    enableLogin(true);
                } else {
                    enableLogin(false);
                }
            }
        });
        editText_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && editText_username.getText().toString().trim().length() > 0) {
                    enableLogin(true);
                } else {
                    enableLogin(false);
                }
            }
        });
    }

    protected void enableLogin(boolean enabled) {
        button_login.setEnabled(enabled);
    }

    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

    public boolean validate(final String password) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public class BackgroundAsync extends AsyncTask<Void, Void, Boolean> {
        String userName, password;

        public BackgroundAsync(String _userName, String _password) {
            userName = _userName;
            password = _password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            pd.setMessage("Please wait...");
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().getLogin(editText_username.getText().toString().trim(), editText_password.getText().toString().trim());
                JSONObject json = new JSONObject(result);
                if (json.has("EmployeeId") && json.getString("EmployeeId").length() > 0) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("employeeID", json.getString("EmployeeId"));
                    if(cb_rememberMe.isChecked()) {
                        editor.putString("userName", editText_username.getText().toString().trim());
                        editor.putString("password", editText_password.getText().toString().trim());
                    } else {
                        editor.putString("userName", "");
                        editor.putString("password", "");
                    }
                    editor.commit();
                    return true;
                }
            } catch (JSONException je) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            pd.dismiss();
            if (s) {
                Intent homeIntent = new Intent(mContext, HomeActivity.class);
                startActivity(homeIntent);
                finish();

            } else {
                mToast.setText("Invalid Username / Password");
                mToast.show();
            }
            super.onPostExecute(s);
        }
    }
}
