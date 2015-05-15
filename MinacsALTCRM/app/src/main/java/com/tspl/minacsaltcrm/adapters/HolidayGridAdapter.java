package com.tspl.minacsaltcrm.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tspl.minacsaltcrm.ClassObject.Holiday;
import com.tspl.minacsaltcrm.R;

public class HolidayGridAdapter extends BaseAdapter {
	Context nContext;
	private List<Holiday> holidayList;
	LayoutInflater inflater;

	public HolidayGridAdapter(Context context) {
		nContext = context;
        holidayList = new ArrayList<Holiday>();
		inflater = LayoutInflater.from(context);
	}

    public void setHolidayList(List<Holiday> holidayList) {
        this.holidayList =new ArrayList<Holiday>(holidayList);
    }

    @Override
	public int getCount() {

		return holidayList.size();
	}

	@Override
	public Object getItem(int position) {

		return holidayList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.holidays_grid_item, null);

			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_month = (TextView) convertView
				.findViewById(R.id.textView_month);
		holder.tv_month.setText(holidayList.get(position).month);
		holder.tv_date = (TextView) convertView
				.findViewById(R.id.textView_date);
		holder.tv_date.setText(holidayList.get(position).date);
		holder.tv_day = (TextView) convertView.findViewById(R.id.textView_day);
		holder.tv_day.setText(holidayList.get(position).day);

		return convertView;
	}

	private class ViewHolder {
		TextView tv_month;
		TextView tv_date;
		TextView tv_day;
	}


}
