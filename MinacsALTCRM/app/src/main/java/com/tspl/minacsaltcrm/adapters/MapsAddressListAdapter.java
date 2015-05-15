package com.tspl.minacsaltcrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tspl.minacsaltcrm.R;

import java.util.ArrayList;
import java.util.List;

public class MapsAddressListAdapter extends BaseAdapter {
    Context nContext;
    List<Address> addressList;
    LayoutInflater inflater;

    public MapsAddressListAdapter(Context context) {
        nContext = context;
        initDatas();
        inflater = LayoutInflater.from(context);
    }

    private void initDatas() {
        addressList = new ArrayList<Address>();

        Address address = new Address();
        address.address1 = "1189 Colonel Sam Drive";
        address.address2 = "Oshawa, Ontario L1H 8W8";
        address.address3 = "Canada.";
        addressList.add(address);
        Address address2 = new Address();
        address2.address1 = "34115 West Twelve Mile Road";
        address2.address2 = "Farmington Hills, Michigan 48331";
        address2.address3 = "USA.";
        addressList.add(address2);

        Address address3 = new Address();
        address3.address1 = "Campus 4A, Ecospace Business Park";
        address3.address2 = "Outer Ring Road, Bellandur";
        address3.address3 = "Bangalore 560103, India.";
        addressList.add(address3);
    }

    @Override
    public int getCount() {

        return addressList.size();
    }

    @Override
    public Object getItem(int position) {

        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.maps_address_list_item,
                    null);

            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_Add1 = (TextView) convertView
                .findViewById(R.id.textView_address1);
        holder.tv_Add1.setText(addressList.get(position).address1);
        holder.tv_Add2 = (TextView) convertView
                .findViewById(R.id.textView_address2);
        holder.tv_Add2.setText(addressList.get(position).address2);
        holder.tv_Add3 = (TextView) convertView
                .findViewById(R.id.textView_address3);
        holder.tv_Add3.setText(addressList.get(position).address3);

        return convertView;
    }

    private class ViewHolder {
        TextView tv_Add1;
        TextView tv_Add2;
        TextView tv_Add3;
    }

    class Address {
        public String address1;
        public String address2;
        public String address3;
    }
}
