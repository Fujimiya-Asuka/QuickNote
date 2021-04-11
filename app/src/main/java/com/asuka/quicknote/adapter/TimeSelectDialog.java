package com.asuka.quicknote.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.asuka.quicknote.R;

import java.util.Calendar;

public class TimeSelectDialog extends Dialog {
    private Context context;
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;
    private TextView submitBtn, cancelBtn;
    private ClickListener clickListener;

    public TimeSelectDialog(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.time_select_layout, null);
        setContentView(view);
        mDatePicker = findViewById(R.id.datePicker);
        mTimePicker =  findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        submitBtn =  findViewById(R.id.submit);
        cancelBtn =  findViewById(R.id.cancel);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(
                        mDatePicker.getYear(),
                        mDatePicker.getMonth(),
                        mDatePicker.getDayOfMonth(),
                        mTimePicker.getHour(),
                        mTimePicker.getMinute(),
                        0
                );
                clickListener.doConfirm(calendar);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.doCancel();
            }
        });
    }

    public interface ClickListener{
        public void doConfirm(Calendar calendar);
        public void doCancel();
    }

}
