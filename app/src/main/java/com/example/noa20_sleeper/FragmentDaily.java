package com.example.noa20_sleeper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentDaily extends Fragment {
    private TextView tv_date;
    private TextView tv_day;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        tv_date = (TextView)view.findViewById(R.id.date);
        tv_day = (TextView)view.findViewById(R.id.day);

        Date currentTime = Calendar.getInstance().getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);
        String day_text = new SimpleDateFormat("EE요일", Locale.getDefault()).format(currentTime);

        tv_date.setText(date_text);
        tv_day.setText(day_text);

        return view;
    }
}