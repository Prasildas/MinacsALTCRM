package com.tspl.minacsaltcrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tspl.minacsaltcrm.ClassObject.LeaveTypes;
import com.tspl.minacsaltcrm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t0396 on 5/6/2015.
 */
public class LeaveTypesSpinner extends BaseAdapter {
    LayoutInflater inflater;
    List<LeaveTypes> leaveTypes;

    public LeaveTypesSpinner(Context ctx) {
        inflater = LayoutInflater.from(ctx);
        leaveTypes = new ArrayList<LeaveTypes>();

    }

    public void setLeaveTypes(List<LeaveTypes> leaveTypes) {
        this.leaveTypes = new ArrayList<LeaveTypes>(leaveTypes);
    }

    @Override
    public int getCount() {

        return leaveTypes.size();
    }

    @Override
    public LeaveTypes getItem(int position) {
        return leaveTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView_Disp(pos, cnvtView, prnt);
    }
    /*
     * getCustomView for the display of dropdownView
    * */
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View mySpinner = inflater.inflate(R.layout.leavetype_spinner_view, parent, false);
        TextView title = (TextView) mySpinner.findViewById(R.id.custom_spinner_item);
        title.setText(leaveTypes.get(position).value);

        return mySpinner;
    }
/*
* getCustomView_Disp for the display of getView
* */
    public View getCustomView_Disp(int position, View convertView, ViewGroup parent) {

        View mySpinner = inflater.inflate(R.layout.cust_spinner_1, parent, false);
        TextView title = (TextView) mySpinner.findViewById(R.id.custom_spinner_item);
        title.setText(leaveTypes.get(position).value);

        return mySpinner;
    }

}

