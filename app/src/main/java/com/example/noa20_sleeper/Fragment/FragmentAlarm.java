package com.example.noa20_sleeper.Fragment;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.noa20_sleeper.Activity.MainActivity;
import com.example.noa20_sleeper.Activity.SleepingActivity;
import com.example.noa20_sleeper.AlarmReceiver;
import com.example.noa20_sleeper.InsertData;
import com.example.noa20_sleeper.PreferenceManager;
import com.example.noa20_sleeper.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

// 네비게이션에서 알람탭
public class FragmentAlarm extends Fragment {
    private static final String TAG = "LOG_TAG";
    private static final String nextNotifyTime_key="nextNotifyTime";
    private TimePicker picker;
    private Button startButton;
    private Calendar nextNotifyTime;
    private Calendar calendar;
    private Intent alarmIntent;
    private Intent sleepingIntent;
    private PendingIntent pendingIntent;
    private Context mContext;

    int hour, hour_24, minute;
    String am_pm;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        Log.d(TAG, "onCreateView: FragmentAlarm created");
        picker = view.findViewById(R.id.timePicker);
        startButton = view.findViewById(R.id.bt_start);
        mContext = this.getContext();

        nextNotifyTime = new GregorianCalendar();
        calendar = Calendar.getInstance();
        alarmIntent = new Intent(this.getActivity(), AlarmReceiver.class);
        sleepingIntent = new Intent(this.getActivity(), SleepingActivity.class);

//        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("daily alarm", getActivity().MODE_PRIVATE);
//        long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());

        long millis = PreferenceManager.getLong(mContext,nextNotifyTime_key);
        if(millis == -1) millis = Calendar.getInstance().getTimeInMillis();

        nextNotifyTime.setTimeInMillis(millis);

        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));

        // SDK 23 이상
        picker.setHour(pre_hour);
        picker.setMinute(pre_minute);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onCreateView: FragmentAlarm button clicked");
                setTime();

//                SharedPreferences.Editor editor = getActivity().getSharedPreferences("daily alarm", getActivity().MODE_PRIVATE).edit();
//                editor.putLong("nextNotifyTime", calendar.getTimeInMillis());
//                editor.apply();

                PreferenceManager.setLong(mContext, nextNotifyTime_key, calendar.getTimeInMillis());

                SimpleDateFormat today = new SimpleDateFormat("HH:mm");
                String settime = today.format(calendar.getTime());
                PreferenceManager.setString(mContext, "alarmTime", ""+settime);

                InsertData task = new InsertData();
                task.execute("settime.php","setTime", settime);


                Log.d(TAG, "onClick: FragmentAlarm "+calendar.getTimeInMillis());

                pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);

                // SDK 23 이상
                ((MainActivity)getActivity()).getAlarmManager().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                startActivity(sleepingIntent);
            }
        });


        return view;
    }

    void setTime(){
        hour_24 = picker.getHour();
        minute = picker.getMinute();

        if (hour_24 > 12) {
            am_pm = "PM";
            hour = hour_24 - 12;
        } else {
            hour = hour_24;
            am_pm = "AM";
        }

        // 현재 지정된 시간으로 알람 시간 설정
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour_24);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
    }
}