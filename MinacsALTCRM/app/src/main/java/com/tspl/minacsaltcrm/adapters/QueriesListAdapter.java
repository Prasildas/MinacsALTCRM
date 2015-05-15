package com.tspl.minacsaltcrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tspl.minacsaltcrm.ClassObject.Holiday;
import com.tspl.minacsaltcrm.R;

import java.util.ArrayList;
import java.util.List;

public class QueriesListAdapter extends BaseAdapter {
    Context nContext;
    private List<String> items;
    LayoutInflater inflater;

    public QueriesListAdapter(Context context) {
        nContext = context;
        initItems();
        inflater = LayoutInflater.from(context);
    }

    /**
     * adding request items
     */
    private void initItems() {
        items = new ArrayList<String>();
        items.add("Change my Optional Holidays");
        items.add("Request for Leave");
        items.add("Request for Callback");
        items.add("Request for Policy");
        items.add("Schedule an Appointment");
        items.add("PF Enquiry");
        items.add("Leave Balance");
    }


    @Override
    public int getCount() {

        return items.size();
    }

    @Override
    public String getItem(int position) {

        return items.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.query_list_item, parent, false);

            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_title = (TextView) convertView
                .findViewById(R.id.tv_QueryItem);
        holder.tv_slNo = (TextView) convertView
                .findViewById(R.id.tv_slNo);
        holder.tv_title.setText(items.get(position));
        int no = position + 1;
        holder.tv_slNo.setText(no + "");
        return convertView;
    }

    private class ViewHolder {
        TextView tv_title;
        TextView tv_slNo;
    }


}
