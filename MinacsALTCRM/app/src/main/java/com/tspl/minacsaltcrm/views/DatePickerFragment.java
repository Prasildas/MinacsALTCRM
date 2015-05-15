package com.tspl.minacsaltcrm.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {
    String daySelected = "";
    private UpdateFromDateDialog updateFromDateDialog = null;
    private int year, month, day;
    private int isCurrentDate = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day) {
            @Override
            public void onDateChanged(DatePicker view, int cyear, int cmonth,
                                      int cday) {
                if (!isPreviousDatesSelectable) {
                    if (cyear < year)
                        view.updateDate(year, month, day);

                    if (cmonth < month && cyear <= year)
                        view.updateDate(year, month, day);

                    if (cday < day && cyear <= year && cmonth <= month)
                        view.updateDate(year, month, day);

                    if (cday == day && cmonth == month && cyear == year) {
                        isCurrentDate = 1;
                    }
                }
            }
        };
        return datePickerDialog;
    }

    boolean isPreviousDatesSelectable = true;

    public void setPreviousDateSelectable(boolean isPreviousDatesSelectable) {
        this.isPreviousDatesSelectable = isPreviousDatesSelectable;

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (view.isShown()) {
            daySelected = getDisplayDate(year, month, day);
            if (updateFromDateDialog != null) {
                updateFromDateDialog.setDate(new String[]{daySelected, getServerFormatDate(year, month, day), isCurrentDate + ""});
            }

        }
    }

    /**
     * @param _updateFromDateDialog Used to communicate with classes, passing result, etc
     */
    public void setDialogDataConnector(UpdateFromDateDialog _updateFromDateDialog) {
        updateFromDateDialog = _updateFromDateDialog;
    }

    /**
     * displaying format
     *
     * @param year
     * @param month
     * @param day
     * @return displaying fromat
     */
    public String getDisplayDate(int year, int month, int day) {
        String dayStr = "", monthStr = "";
        dayStr = day + "";
        if (dayStr.length() < 2) {
            dayStr = "0" + day;
        } else {
            dayStr = "" + day;
        }
        month = month + 1;
        monthStr = month + "";
        if (monthStr.length() < 2) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }
        return dayStr + "/" + monthStr + "/" + year;
    }

    public String getServerFormatDate(int year, int month, int day) {
//		yyyy-MM-dd HH:mm:ss
        String dayStr = "", monthStr = "";
        dayStr = day + "";
        if (dayStr.length() < 2) {
            dayStr = "0" + day;
        } else {
            dayStr = "" + day;
        }
        month = month + 1;
        monthStr = month + "";
        if (monthStr.length() < 2) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }
        return dayStr + "/" + monthStr + "/" + year;
    }

    /**
     * @return current data
     */
    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return getDisplayDate(year, month, day);
    }

    public String getLastUpdatedDate() {
        return daySelected;
    }

    public interface UpdateFromDateDialog {
        public void setDate(String[] date);
    }


}
