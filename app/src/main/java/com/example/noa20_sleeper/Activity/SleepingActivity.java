package com.example.noa20_sleeper.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noa20_sleeper.AlarmReceiver;
import com.example.noa20_sleeper.R;

// 사용자가 알람을 설정하고 잠에 들었을 때 나오는 화면
public class SleepingActivity extends AppCompatActivity {
    private static final String TAG = "LOG_TAG";
    public static Activity _SleepingActivity;

    private Button cancelButton;
    private PendingIntent pendingIntent;
    private Intent alarmIntent;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeping);

        Log.d(TAG, "onCreate: SleepingActivity created");
        _SleepingActivity = SleepingActivity.this;

        mContext = this;
        alarmIntent = new Intent(this, AlarmReceiver.class);

        cancelButton = findViewById(R.id.bt_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d(TAG, "onCreate: SleepingActivity button clicked");
                pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);

                ((MainActivity)MainActivity.mContext).getAlarmManager().cancel(pendingIntent);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
