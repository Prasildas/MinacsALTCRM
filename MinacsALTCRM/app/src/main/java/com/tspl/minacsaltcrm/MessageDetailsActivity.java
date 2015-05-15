package com.tspl.minacsaltcrm;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Displaying the message to read it fully
 */
public class MessageDetailsActivity extends BaseActivity {
    Context context;

    TextView title;
    TextView subject;
    LinearLayout message_outerLayer;
    TextView message;
    TextView time;
    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        context = getBaseContext();
        super.bindFooterViews(context);

        title = (TextView) findViewById(R.id.textView_title);
        subject = (TextView) findViewById(R.id.textView_subject);
        message_outerLayer = (LinearLayout) findViewById(R.id.message_outerLayer);
        message = (TextView) findViewById(R.id.textView_message);
        time = (TextView) findViewById(R.id.textView_time);
        date = (TextView) findViewById(R.id.textView_date);
        title.setText(Pr.messageItem.senderName);
        message.setText(Pr.messageItem.messageContent);
        date.setText(Pr.messageItem.createdOn);
        time.setText(Pr.messageItem.time);
        subject.setText(Pr.messageItem.subject);
    }
}
