package com.tspl.minacsaltcrm;

import android.util.Log;

import com.tspl.minacsaltcrm.ClassObject.MessageItem;

/**
 * Created by t0396 on 4/28/2015.
 * Class used to manage log
 */
public class Pr {
    public static void ln(String msg) {
        Log.v("Minacs", ":Minacs:- " + msg);
    }

    static MessageItem messageItem;
}
