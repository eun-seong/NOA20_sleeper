package com.example.noa20_sleeper.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noa20_sleeper.AlarmReceiver;
import com.example.noa20_sleeper.AudioReader;
import com.example.noa20_sleeper.InsertData;
import com.example.noa20_sleeper.PreferenceManager;
import com.example.noa20_sleeper.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

// 사용자가 알람을 설정하고 잠에 들었을 때 나오는 화면
public class SleepingActivity extends AppCompatActivity {
    public static Activity _SleepingActivity;
    private static final String TAG = "LOG_TAG";
    private int sumOfDB;
    private int cnt;
    private long now;

    private Timer timer;
    private PendingIntent pendingIntent;
    private Intent alarmIntent;
    private Context mContext;
    private Date date;
    private String LatestDate;
    private double level;

    private AudioReader audioReader;
    private final int mSampleRate = 8000;
    private final int inputBlockSize = 256;
    private final int sampleDecimate = 1;

    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeping);

        Log.d(TAG, "onCreate: SleepingActivity created");
        _SleepingActivity = SleepingActivity.this;

        mContext = this;
        timer = new Timer();
        alarmIntent = new Intent(this, AlarmReceiver.class);
        cancelButton = findViewById(R.id.bt_cancel);
        sumOfDB = 0; cnt = 0;
        audioReader = new AudioReader();

        now = System.currentTimeMillis();
        date = new Date(now);

        SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
        LatestDate = today.format(date);
        Log.d(TAG, "onCreate: SleepingActivity : Date "+LatestDate);


        // TODO 사용자의 환경 기본 소음이 얼마인지 알아내기
        // 아마도 처음 5~10분 기준?

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: SleepingActivity calculate");
                level = sumOfDB/cnt;

                /* TODO status의 범위 지정
                 0 : 깨어남
                 1 : 얕은 수면
                 2 : 깊은 수면
                */

                SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                String strNow = time.format(date);
                Log.d(TAG, "run: SleepingActivity time : "+strNow+ " level : "+level);

                InsertData task = new InsertData();
                task.execute("addData.php","date",LatestDate,"time",""+strNow,"level",""+level);

                sumOfDB=0;
                cnt=0;
            }
        };

        doStart();
        timer.schedule(timerTask, 10000, 300000); //Timer 실행

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d(TAG, "onCreate: SleepingActivity button clicked");

                doStop();
                timer.cancel();//타이머 종료

                InsertData task = new InsertData();
                task.execute("clearData.php","filename",LatestDate);

                pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
                ((MainActivity)MainActivity.mContext).getAlarmManager().cancel(pendingIntent);
                finish();
            }
        });
    }

    public void doStart()
    {
        audioReader.startReader(mSampleRate, inputBlockSize * sampleDecimate, new AudioReader.Listener()
        {
            @Override
            public final void onReadComplete(int dB)
            {
                receiveDecibel(dB);
                sumOfDB+=dB;
                cnt++;
            }

            @Override
            public void onReadError(int error)
            {

            }
        });
    }

    private void receiveDecibel(final int dB)
    {
        Log.e("### dB", dB+" dB");

    }

    public void doStop() {
        audioReader.stopReader();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    /***************************************************************************/

    class WebBridge {
        @JavascriptInterface
        public void SuccessArrival() {  // 목적지에 도착할 경우 실행
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

}


