package com.example.noa20_sleeper.Activity;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noa20_sleeper.R;

// 알람이 울릴 때 화면
public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "LOG_TAG";
    private Button wakeupButton;
    private Vibrator vibrator;
    private Uri uri;
    private Ringtone ringtone;
    private AudioAttributes audioAttributes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Log.d(TAG, "onCreate: AlarmActivity create");
        long[] pattern = {500, 2000, 500, 1000 };
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build();
        wakeupButton = findViewById(R.id.bt_wakeup);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        // 잠금화면 위에서도 사용할수있게
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        vibrator.vibrate(pattern, 0);
        ringtone.setAudioAttributes(audioAttributes);
        ringtone.play();

        // TODO 저장된 데이터 업데이트

        wakeupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: AlarmActivity button clicked");
                vibrator.cancel();
                if(ringtone.isPlaying()) ringtone.stop();

                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
