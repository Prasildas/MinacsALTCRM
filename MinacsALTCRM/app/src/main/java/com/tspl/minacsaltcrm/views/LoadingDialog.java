package com.tspl.minacsaltcrm.views;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tspl.minacsaltcrm.R;

/**
 * Created by Vishnu Mohan on 29-04-2015.
 */
public class LoadingDialog extends Dialog {

    private View view;
    private Context context;
    private String title;
    private String message;
    private TextView tvTitle;
    private TextView tvMessage;
    private LayoutInflater layoutInflater;

    public LoadingDialog(Context context, String title,
                         String message) {
        super(context);
        this.context = context;
        this.title = title;
        this.message = message;
        initView();
    }

    private void initView() {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.loading_dialog_layout, null);

        tvTitle = (TextView) view.findViewById(R.id.tvDialogTitle);
        tvMessage = (TextView) view.findViewById(R.id.tvDialogMessage);

        tvTitle.setText(title);
        tvMessage.setText(message);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        setCanceledOnTouchOutside(false);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    public void setVisible(boolean visible) {
        if (visible) {
            show();
        } else {
            dismiss();
        }
    }

}
