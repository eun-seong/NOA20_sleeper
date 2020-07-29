package com.example.noa20_sleeper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FragmentSetting extends Fragment {
    private static final String TAG = "LOG_TAG";
    private static final String nextNotifyTime_key="nextNotifyTime";
    private TimePicker picker;
    private Button startButton;
    private Calendar nextNotifyTime;
    private Calendar calendar;

    private Intent dssleepIntent;
    private Context mContext;

    int hour, hour_24, minute;
    String am_pm;

    private SeekBar sb;
    TextView tv_val;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        Log.d(TAG, "onCreateView: FragmentSetting created");

        super.onCreateView(inflater, container, savedInstanceState);

        sb=(SeekBar) view.findViewById(R.id.seekbar);
        tv_val=(TextView) view.findViewById(R.id.tv_val);
        sb.getProgressDrawable().setColorFilter( Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
        sb.getThumb().setColorFilter(Color.parseColor("#00BFFF"), PorterDuff.Mode.SRC_IN );


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                InsertData task = new InsertData();
                task.execute("setBright.php", getString(R.string.COL_BRIGHT), Integer.toString(progress));

                tv_val.setText(" 수면등 밝기 : " + progress);

            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        picker = view.findViewById(R.id.timePicker);
        picker.setIs24HourView(true);


        startButton = view.findViewById(R.id.desired_sleep);

        mContext = this.getContext();
        nextNotifyTime = new GregorianCalendar();
        calendar = Calendar.getInstance();

        long millis = PreferenceManager.getLong(mContext, nextNotifyTime_key);
        if(millis == -1) millis = Calendar.getInstance().getTimeInMillis();

        nextNotifyTime.setTimeInMillis(millis);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onCreateView: FragmentSetting button clicked");
                setTime();

                int hopeTime = hour_24*60 + minute;
                InsertData task = new InsertData();

                task.execute("setHopeTime.php",
                        getString(R.string.COL_HOPETIME), Integer.toString(hopeTime));

                Toast.makeText(mContext.getApplicationContext()," 희망 수면 시간이 설정되었습니다!", Toast.LENGTH_SHORT).show();
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
            calendar.add(Calendar.DATE, 0);
        }
    }


}
