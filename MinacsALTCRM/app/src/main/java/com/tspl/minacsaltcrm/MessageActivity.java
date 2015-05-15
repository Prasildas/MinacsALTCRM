package com.tspl.minacsaltcrm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tspl.minacsaltcrm.ClassObject.MessageItem;
import com.tspl.minacsaltcrm.adapters.MessageAdapter;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends BaseActivity {
    private SharedPreferences preferences;
    Context mContext;
    ListView listView_main;
    MessageAdapter msgAdapter;
    List<MessageItem> messages;
    private TextView tvNoMessages;
    boolean isUpdatingFlag = false, loadingMoreMessages = false;
    String employeeID = "";
    int pageCnt = 0;
    public int pageNo;
    UpdateUnReadCntBackgroundAsync updateUnReadCntBackgroundAsync = null;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initData();
        setContentView(R.layout.messages);
        listView_main = (ListView) findViewById(R.id.listView_main);

        tvNoMessages = (TextView) findViewById(R.id.tvNoMessages);
        listView_main.setAdapter(msgAdapter);
        clickListners();
        super.bindFooterViews(mContext);
        BackgroundAsync backgroundAsync = new BackgroundAsync(employeeID, 1);
        backgroundAsync.execute();

    }
    /**
     * Initialise data
     */
    private void initData() {
        msgAdapter = new MessageAdapter(mContext);
        messages = new ArrayList<MessageItem>();
        employeeID = preferences.getString("employeeID", "");
    }

    /**
     * Initialise
     */
    private void init() {
        mContext = this;
        preferences = getSharedPreferences(AppConstants.PREFERENCE_CONSTANT, MODE_PRIVATE);
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);

    }

    /**
     * Binding click events
     */
    private void clickListners() {

        listView_main.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                //is the bottom item visible & not loading more already ? Load more !
                if (totalItemCount != 0 && (lastInScreen == totalItemCount) && !loadingMoreMessages) {
                    BackgroundAsync backgroundAsync = new BackgroundAsync(employeeID, pageCnt + 1);
                    backgroundAsync.execute();
                }
            }
        });

        listView_main.setOnItemClickListener(new OnItemClickListener() {
            Intent intent;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateRead(position);
                Pr.messageItem = messages.get(position);
                intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /**
     * updateRead
     * updating to server for the message read flag
     * @param position
     */
    private void updateRead(int position) {
        MessageItem msgItem = messages.get(position);
        if (msgItem.readStatus != 1 && !isUpdatingFlag) {
            updateUnReadCntBackgroundAsync = new UpdateUnReadCntBackgroundAsync(employeeID, msgItem.messageId, position);
            updateUnReadCntBackgroundAsync.execute();
        }

    }

    /**
     * updating to server for the message read flag
     */
    public class BackgroundAsync extends AsyncTask<Void, Void, Integer> {
        private String employeeID;
        ProgressDialog pd;

        public BackgroundAsync(String _employeeID, int _pageNo) {
            employeeID = _employeeID;
            pageNo = _pageNo;
            pd = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            loadingMoreMessages = true;
            pd.setCancelable(false);
            pd.setMessage("Please wait...");
            if (pageNo <= 1) {
                pd.show();
            }
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().getMessages(employeeID, pageNo);
                JSONObject messageObj = new JSONObject(result);
                if (messageObj.has("Acknowledge") && messageObj.getInt("Acknowledge") == 1) {
                    if (messageObj.has("SMS")) {
                        JSONArray messagesJsonArr = messageObj.getJSONArray("SMS");
                        for (int i = 0; i < messagesJsonArr.length(); i++) {
                            JSONObject msgJson = messagesJsonArr.getJSONObject(i);
                            MessageItem msgItem = new MessageItem();
                            msgItem.readStatus = msgJson.getInt("ReadStatus");
                            msgItem.messageId = msgJson.getInt("MessageId");
                            msgItem.messageContent = msgJson.getString("MessageContent");
                            msgItem.createdOn = msgJson.getString("CreatedOn");
                            if (!msgJson.isNull("Subject"))
                                msgItem.subject = msgJson.getString("Subject");
                            msgItem.senderName = msgJson.getString("SenderName");
                            messages.add(msgItem);
                        }
                        int remainder = messages.size() % AppConstants.MG_PER_PAGE_CONSTANT;
                        pageCnt = messages.size() / AppConstants.MG_PER_PAGE_CONSTANT;
                        if (remainder > 0) {
                            pageCnt += 1;
                        }
                        return messagesJsonArr.length();
                    }
                }
            } catch (JSONException je) {

            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer i) {
            loadingMoreMessages = false;
            if (pd.isShowing()) {
                pd.dismiss();
            }

            if (i == 0) {

            } else if (i == -1) {
                mToast.setText("Failed to update Messages.");
                mToast.show();
            } else {
                msgAdapter.setDatas(messages);
                msgAdapter.notifyDataSetChanged();
            }

            super.onPostExecute(i);
        }
    }

    public class UpdateUnReadCntBackgroundAsync extends AsyncTask<Void, Void, Boolean> {
        private String employeeID;
        private long messageId;
        private int position;

        public UpdateUnReadCntBackgroundAsync(String _employeeID, long _messageId, int _position) {
            employeeID = _employeeID;
            messageId = _messageId;
            position = _position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().updateMessageRead(employeeID, messageId);
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
                messages.get(position).readStatus = 1;
                msgAdapter.notifyDataSetChanged();
            }
            super.onPostExecute(i);
        }
    }
}
