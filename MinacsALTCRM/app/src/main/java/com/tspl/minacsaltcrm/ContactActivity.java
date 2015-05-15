package com.tspl.minacsaltcrm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ContactActivity extends BaseActivity implements View.OnClickListener {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mContext = ContactActivity.this;
        bindFooterViews(mContext);
        (findViewById(R.id.button_contact)).setEnabled(false);

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

    /**
     * Binding footer views
     * @param _context
     */
    protected void bindFooterViews(Context _context) {
        mContext = _context;
        /*Footer buttons*/
        button_home = (Button) findViewById(R.id.button_home);
        button_myProfile = (Button) findViewById(R.id.button_myProfile);
        button_social = (Button) findViewById(R.id.button_social);
        button_contact = (Button) findViewById(R.id.button_contact);
        clickListner();
    }

    /**
     * Adding click listners
     */
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

}
