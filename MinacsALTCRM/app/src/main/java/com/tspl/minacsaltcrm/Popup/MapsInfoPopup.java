package com.tspl.minacsaltcrm.Popup;

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

import com.tspl.minacsaltcrm.MapsActivity;
import com.tspl.minacsaltcrm.R;
import com.tspl.minacsaltcrm.webservices.SoapHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by t0396 on 4/27/2015.
 */
public class MapsInfoPopup extends PopupWindow {
    String title, details;
    private LayoutInflater layoutInflater;
    private Context context;
    private View view;

    private Toast mToast;
    private Button btnApply, btnCancel;
    public EditText etSelectDate;

    private EditText etComments;
    private String employeeID;
    private int locationId;

    TextView tvAddress, tvName;

    public MapsInfoPopup(Context _context) {
        context = _context;

        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        view = layoutInflater.inflate(R.layout.popup_map_infowindow,
                null);
        setContentView(view);
        initData();

        btnApply = (Button) view.findViewById(R.id.btnApply);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        etSelectDate = (EditText) view.findViewById(R.id.etSelectDate);
        etComments = (EditText) view.findViewById(R.id.etComments);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        tvName = (TextView) view.findViewById(R.id.tvName);
        setBackgroundDrawable(new BitmapDrawable());
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);

        clickListners();

    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    private void initData() {

    }

    public void setData(String name, String addrs) {
        tvAddress.setText(addrs);
        tvName.setText(name);
    }

    /**
     * binding click events
     */
    private void clickListners() {


        etSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = ((MapsActivity) context).showDateDialog();
                etSelectDate.setText(selectedDate);
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bookAppnt();
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
     * to book appointment
     */
    private void bookAppnt() {
        BookApptmntBackgroundAsync bookApptmntBackgroundAsync = new BookApptmntBackgroundAsync(employeeID, etComments.getText().toString().trim(), locationId, etSelectDate.getText().toString().trim());
        bookApptmntBackgroundAsync.execute();
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void addDetails(String title, String details) {
        this.title = title;
        this.details = details;
    }

    /**
     * to show and hide popup
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (visible) {
            showAtLocation(view, Gravity.CENTER, 50, 0);

        } else {
            dismiss();
        }
    }

    /*public void setVisibleAt(int x, int y) {
        showAtLocation(view, Gravity.CENTER, x, y);
    }*/


    public class BookApptmntBackgroundAsync extends AsyncTask<Void, Void, Boolean> {
        private String employeeID;
        //        "EmployeeId":"047841","MessageId":42
        private int locationId;
        private String comment, scheduleDate;

        public BookApptmntBackgroundAsync(String _employeeID, String _comment, int _locationId, String _scheduleDate) {
            employeeID = _employeeID;
            comment = _comment;
            locationId = _locationId;
            scheduleDate = _scheduleDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String result = "";
            try {
                result = new SoapHandler().setAppointment(employeeID, comment, locationId, scheduleDate);
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
            Toast.makeText(context, "Appointment added Successfully", Toast.LENGTH_SHORT).show();
            dismiss();
            super.onPostExecute(i);

        }
    }

}
