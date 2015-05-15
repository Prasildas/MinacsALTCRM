package com.tspl.minacsaltcrm.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {
    String timeSelected = "";
    private UpdateFromTimeDialog updateFromTimeDialog = null;
    int hour, minute;
    private boolean isCurrDateSet = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity())) {
            @Override
            public void onTimeChanged(TimePicker view, int chourOfDay,
                                      int cminute) {
                if (isCurrDateSet) {
                    if (chourOfDay < hour) {
                        view.setCurrentHour(hour);
                    }
                    if (cminute < minute && chourOfDay == hour) {
                        view.setCurrentMinute(minute);
                    }
                }
            }
        };
        return timePickerDialog;
    }

    public void setDialogTimeConnector(
            UpdateFromTimeDialog _updateFromTimeDialog) {
        updateFromTimeDialog = _updateFromTimeDialog;
    }

    public String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        return getDisplayTime(hour, minute);
    }

    public String getLastUpdatedTime() {
        return timeSelected;
    }

    public String getDisplayTime(int hourOfDay, int minute) {
        String timeDisp = "", minuteStr = "", hourOfDayStr = "", timeSectn = "";
        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = "" + minute;
        }
        if (hourOfDay > 12) {
            timeSectn = " PM";
            hourOfDay = hourOfDay - 12;
        } else {
            timeSectn = " AM";
        }

        if (hourOfDay < 10) {
            hourOfDayStr = "0" + hourOfDay;
        } else {
            hourOfDayStr = "" + hourOfDay;
        }

        timeDisp = hourOfDayStr + ":" + minuteStr + timeSectn;
        return timeDisp;

    }

    public interface UpdateFromTimeDialog {
        public void setTime(String[] timeArr);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeSelected = getDisplayTime(hourOfDay, minute);
        if (updateFromTimeDialog != null) {
            updateFromTimeDialog.setTime(new String[]{timeSelected, getServerFormattedTime(hourOfDay, minute)});
        }

    }

    public String getServerFormattedTime(int hourOfDay, int minute) {
        String timeDisp = "", minuteStr = "", hourOfDayStr = "", timeSectn = "";
        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = "" + minute;
        }
        if (hourOfDay < 10) {
            hourOfDayStr = "0" + hourOfDay;
        } else {
            hourOfDayStr = "" + hourOfDay;
        }
//		yyyy-MM-dd HH:mm:ss
        timeDisp = hourOfDayStr + ":" + minuteStr + ":00";
        return timeDisp;

    }

    public void setCurrentTimeValidation(int _isCurrDateSet) {
        if (_isCurrDateSet == 1) {
            isCurrDateSet = true;
        } else {
            isCurrDateSet = false;
        }

    }


}
