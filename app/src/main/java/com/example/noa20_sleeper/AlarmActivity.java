package com.example.noa20_sleeper;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// 알람이 울릴 때 화면
public class AlarmActivity extends AppCompatActivity {
    private Button wakeupButton;
    private Vibrator vibrator;
    private Uri uri;
    private Ringtone ringtone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        long[] pattern = {50,2000, 50, 1000 };
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        wakeupButton = findViewById(R.id.bt_wakeup);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        // 잠금화면 위에서도 사용할수있게
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);


        vibrator.vibrate(pattern, 0);
        // TODO 벨이 안울림
        ringtone.play();

        wakeupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                vibrator.cancel();
                ringtone.stop();

                finish();
            }
        });

        // TODO 알람이 울리면 SleepingAcitivy 종료
    }
}
