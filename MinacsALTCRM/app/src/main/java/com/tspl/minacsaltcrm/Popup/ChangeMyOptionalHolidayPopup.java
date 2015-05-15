package com.tspl.minacsaltcrm.Popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tspl.minacsaltcrm.ClassObject.Holiday;
import com.tspl.minacsaltcrm.R;
import com.tspl.minacsaltcrm.views.DatePickerFragment;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by t0396 on 4/27/2015.
 */
public class ChangeMyOptionalHolidayPopup extends PopupWindow {
    String title, details;
    private LayoutInflater layoutInflater;
    private Context context;
    private View view;

    protected Toast mToast;
    private Button btnApply, btnCancel;
    public EditText etSelectDate;

    private EditText etComments;
    private String employeeID;
    private int locationId;


    TextView tvName;
    Holiday selectedHoliday;

    public ChangeMyOptionalHolidayPopup(Context _context) {
        context = _context;

        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        view = layoutInflater.inflate(R.layout.changemyoptionalholiday_popup,
                null);
        setContentView(view);
        initData();

        btnApply = (Button) view.findViewById(R.id.btnApply);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        etSelectDate = (EditText) view.findViewById(R.id.etSelectDate);

        etComments = (EditText) view.findViewById(R.id.etComments);
        tvName = (TextView) view.findViewById(R.id.tvName);
        setBackgroundDrawable(new BitmapDrawable());
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);

        clickListners();

    }

    public void setSelectedHoliday(Holiday selectedHoliday) {
        this.selectedHoliday = selectedHoliday;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    private void initData() {

    }

    /**
     * set the heading
     * @param name
     */
    public void setData(String name) {
        tvName.setText(name);
    }

    /**
     * binding click events
     */
    private void clickListners() {


        etSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();

            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeMyOptionalHoliday();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setVisible(false);
            }
        });
    }

    /**
     * validating and update to server
     */
    private void changeMyOptionalHoliday() {
        try {
            String[] dt = selectedHoliday.mmddyy.split(" ");
            selectedHoliday.mmddyy = dt[0];
        } catch (Exception e) {
            selectedHoliday.mmddyy = "";
        }
        if (etSelectDate.getText().toString().trim().length() <= 0) {
            mToast.setText("Please select alternate date");
            mToast.show();
        } else if (etComments.getText().toString().trim().length() <= 0) {
            mToast.setText("Please add Comments");
            mToast.show();
        } else if (selectedHoliday.mmddyy.length() <= 0) {

        } else {
            UpdateOptionalHolidayBackgroundAsync updateOptionalHolidayBackgroundAsync = new UpdateOptionalHolidayBackgroundAsync(employeeID);
            updateOptionalHolidayBackgroundAsync.execute();
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void addDetails(String title) {
        this.title = title;
    }

    public void setVisible(boolean visible) {
        if (visible) {
            showAtLocation(view, Gravity.CENTER, 50, 0);
            etSelectDate.setText("");
        } else {
            dismiss();
        }
    }

    public void setVisibleAt(int x, int y) {
        showAtLocation(view, Gravity.CENTER, x, y);
    }


    public class UpdateOptionalHolidayBackgroundAsync extends AsyncTask<Void, Void, Boolean> {
        private String employeeID;
        private int locationId;
        private String comment, scheduleDate;

        public UpdateOptionalHolidayBackgroundAsync(String _employeeID) {
            employeeID = _employeeID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().updateOptionalHolidays(employeeID, etSelectDate.getText().toString().trim(), etComments.getText().toString().trim(), selectedHoliday.mmddyy);
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
                mToast.setText("Request to change optional holiday has been sent");
                mToast.show();
            } else {
                mToast.setText("Failed to change optional holiday");
                mToast.show();
            }
            dismiss();
            super.onPostExecute(i);

        }
    }


    public String showDateDialog() {
        final String selectedDate[] = new String[1];
        android.app.FragmentManager fm = ((Activity) context).getFragmentManager();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setPreviousDateSelectable(false);
        datePickerFragment.show(fm, "date");

        datePickerFragment.setDialogDataConnector(new DatePickerFragment.UpdateFromDateDialog() {
            @Override
            public void setDate(String[] date) {
                selectedDate[0] = date[0];
                etSelectDate.setText(selectedDate[0]);
            }
        });
        return selectedDate[0];
    }

}
