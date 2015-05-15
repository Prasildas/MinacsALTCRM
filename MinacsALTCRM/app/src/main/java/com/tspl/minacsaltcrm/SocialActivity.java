package com.tspl.minacsaltcrm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

public class SocialActivity extends BaseActivity {
    Context mContext;
    public ImageView btn_facebook;
    public ImageView btn_linkdin, btn_twitter, btn_youtube, btn_slideshare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initViews();
        clickListeners();
        super.bindFooterViews(mContext);
        button_social.setEnabled(false);
    }

    /**
     * Initialise
     */
    private void init() {
        mContext = SocialActivity.this;
    }
    /**
     * Initialise views
     */
    private void initViews() {
        setContentView(R.layout.social);
        btn_facebook = (ImageView) findViewById(R.id.btn_facebook);
        btn_twitter = (ImageView) findViewById(R.id.btn_twitter);
        btn_linkdin = (ImageView) findViewById(R.id.btn_linkdin);
        btn_youtube = (ImageView) findViewById(R.id.btn_youtube);
        btn_slideshare = (ImageView) findViewById(R.id.btn_slideshare);
    }
    /**
     * bind click events
     */
    public void clickListeners() {
        btn_facebook.setOnClickListener(facebookClickListeners);
        btn_twitter.setOnClickListener(twitterClickListeners);
        btn_linkdin.setOnClickListener(linkdinClickListeners);
        btn_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(openYoutube());
            }
        });
        btn_slideshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(openSlideshare());
            }
        });
    }

    private View.OnClickListener facebookClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFacebook();
        }
    };

    private View.OnClickListener twitterClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent twitIntent = openTwitter();
            startActivity(twitIntent);
        }
    };

    private View.OnClickListener linkdinClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openLinkedin();
        }
    };

    /**
     * Open facebook link
     */
    public void openFacebook() {

        String facebookUrl = "https://www.facebook.com/MinacsNorthAmerica";
        try {
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                ;
            } else {
                // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/MinacsNorthAmerica")));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Facebook is not installed. Open the browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
    }
    /**
     * Open twitter link
     */
    public Intent openTwitter() {
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=MinacsGroup"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/MinacsGroup"));
        }
    }
    /**
     * Open Linkedin link
     */
    public void openLinkedin() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://the-minacs-group"));
        final List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/the-minacs-group"));
        }
        startActivity(intent);
    }
    /**
     * Open youtube link
     */
    public Intent openYoutube() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/MinacsVideo"));
    }
    /**
     * Open slideshare link
     */
    public Intent openSlideshare() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.slideshare.net/Minacs"));
    }


}
