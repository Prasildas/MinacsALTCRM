package com.tspl.minacsaltcrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tspl.minacsaltcrm.ClassObject.MessageItem;
import com.tspl.minacsaltcrm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    Context mContext;
    List<MessageItem> messageList;
    LayoutInflater inflater;

    public MessageAdapter(Context context) {
        mContext = context;
        initDatas();
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<MessageItem> _messageList) {
        messageList = new ArrayList<MessageItem>(_messageList);

    }

    private void initDatas() {
        messageList = new ArrayList<MessageItem>();
    }

    @Override
    public int getCount() {

        return messageList.size();
    }

    @Override
    public Object getItem(int position) {

        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.message_list_item, parent, false);

                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title = (TextView) convertView.findViewById(R.id.textView_title);
            holder.subject = (TextView) convertView.findViewById(R.id.textView_subject);
            holder.message_outerLayer = (LinearLayout) convertView.findViewById(R.id.message_outerLayer);
            holder.message = (TextView) convertView.findViewById(R.id.textView_message);
            holder.time = (TextView) convertView.findViewById(R.id.textView_time);
            holder.date = (TextView) convertView.findViewById(R.id.textView_date);
            holder.title.setText(messageList.get(position).senderName);
            holder.message.setText(messageList.get(position).messageContent);
            String time = parseDate(messageList.get(position).createdOn);
            try {
                if (time.length() > 0) {
                    String[] dateTime = time.split(" ");
                    holder.time.setText(dateTime[0]);
                    if (dateTime.length > 1) {
                        holder.date.setVisibility(View.VISIBLE);
                        holder.date.setText(dateTime[1]);
                    } else {
                        holder.date.setVisibility(View.INVISIBLE);
                    }
                    holder.time.setText(dateTime[1].substring(0, dateTime[1].length() - 3));
                    holder.date.setText("Sent on : " + dateTime[0]);
                }
            } catch (Exception e) {

            }

            if (messageList.get(position).subject != null) {
                holder.subject.setText("Sub : " + messageList.get(position).subject);
            } else {
                holder.subject.setText("Sub : ");
            }
            if (messageList.get(position).readStatus == 1) {
                holder.message_outerLayer.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_read));
            } else {
                holder.message_outerLayer.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_unread));
            }
        } catch (Exception e) {

        }
        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView subject;
        TextView message;
        TextView time;
        TextView date;
        LinearLayout message_outerLayer;
    }

    /**
     * parse date
     * @param time
     * @return
     */
    public String parseDate(String time) {
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        String[] dateTime = time.split(" ");

        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(dateTime[0]);

            if (new Date(System.currentTimeMillis()).compareTo(date) == 0) {
                return dateTime[1];
            }
        } catch (ParseException e) {

        }
        return time;
    }



}
