package com.example.noa20_sleeper.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noa20_sleeper.R;
import com.github.mikephil.charting.charts.LineChart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// 네비게이션에서 지난 밤 탭
public class FragmentDaily extends Fragment {
    private static final String TAG = "LOG_TAG";
    private TextView tv_date;
    private TextView tv_day;
    private LineChart dailyChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);

        Log.d(TAG, "onCreateView: FragmentDaily created");
        tv_date = view.findViewById(R.id.date);
        tv_day = view.findViewById(R.id.day);
        dailyChart = view.findViewById(R.id.daily_chart);

        Date currentTime = Calendar.getInstance().getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);
        String day_text = new SimpleDateFormat("EE요일", Locale.getDefault()).format(currentTime);

        tv_date.setText(date_text);
        tv_day.setText(day_text);

        // TODO 제일 최신의 csv 파일을 가져와서 읽기
        // TODO 그래프 그리기
        // TODO 수면 관련 데이터 계산하기

        return view;
    }
}