package com.tspl.minacsaltcrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class BaseActivity extends Activity implements View.OnClickListener {
    public boolean D = false;
    protected Button button_home, button_myProfile, button_social, button_contact;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
    }

    protected void bindFooterViews(Context _context) {
        mContext = _context;
        /*Footer buttons*/
        button_home = (Button) findViewById(R.id.button_home);
        button_myProfile = (Button) findViewById(R.id.button_myProfile);
        button_social = (Button) findViewById(R.id.button_social);
        button_contact = (Button) findViewById(R.id.button_contact);
        clickListner();
    }

    private void clickListner() {
        if (button_home != null)
            button_home.setOnClickListener(this);
        if (button_myProfile != null)
            button_myProfile.setOnClickListener(this);
        if (button_social != null)
            button_social.setOnClickListener(this);
        if (button_contact != null)
            button_contact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
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
