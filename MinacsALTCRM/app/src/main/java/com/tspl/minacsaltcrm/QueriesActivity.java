package com.tspl.minacsaltcrm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tspl.minacsaltcrm.Fragments.RequestFragments;

public class QueriesActivity extends BaseFragmentActivity implements View.OnClickListener {

    Context mContext;
    Toast mToast;
    private SharedPreferences preferences;

    boolean isRunning = false;
    ImageButton imageButton_Leave, imageButton_PF;
    protected Button button_home, button_myProfile, button_social, button_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        init();
        initData();
        initViews();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new RequestFragments()).commit();

    }

    /**
     * Initialise
     */
    private void init() {
        mContext = QueriesActivity.this;
        preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
    }
    /**
     * Initialise data
     */
    private void initData() {

    }
    /**
     * Initialise views
     */
    private void initViews() {
        setContentView(R.layout.queries_outer);

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
     * managing back action to go back to previous fragment
     */
    public void goBack() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new RequestFragments()).commit();
    }

    Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


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


}
