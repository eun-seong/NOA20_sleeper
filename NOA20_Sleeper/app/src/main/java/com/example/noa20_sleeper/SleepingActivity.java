package com.example.noa20_sleeper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private double level;

    private AudioReader audioReader;
    private final int mSampleRate = 8000;
    private final int inputBlockSize = 256;
    private final int sampleDecimate = 1;

    private Button cancelButton;
    private SQLiteDatabase sqliteDB = null ;

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

        DBinit();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: SleepingActivity calculate");

                level = sumOfDB/cnt + Integer.parseInt(getString(R.string.PLUSLEVEL));
                SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                now = System.currentTimeMillis();
                date = new Date(now);
                String strNow = time.format(date);

                String sqlUpdate = "INSERT OR REPLACE INTO "+ getString(R.string.TABLE_NAME_TODAY) + "("+
                        getString(R.string.TABLE_COL_TIME) +", "+
                        getString(R.string.TABLE_COL_LEVEL) +") VALUES ('" +
                        strNow +"', "+
                        level +")";
                sqliteDB.execSQL(sqlUpdate);

                sumOfDB=0;
                cnt=0;

                long setTime = PreferenceManager.getLong(mContext, "nextNotifyTime");

                Log.d(TAG, "run:\tsetTime: "+setTime+"\tnow: "+now+"\ttime: "+strNow+"\tlevel: "+level);

                int userStatus = (level<Integer.parseInt(getString(R.string.INT_DEEP))?2:
                        (level<Integer.parseInt(getString(R.string.INT_SHALLOW))?1:0));

                InsertData task = new InsertData();
                task.execute("setStatus.php",
                        getString(R.string.COL_USERSTATUS), Integer.toString(userStatus));

//                if(setTime-1800000 < now && level>Integer.parseInt(getString(R.string.INT_SHALLOW))){
//                    Log.d(TAG, "run: -----------------------------alarm-----------------------------");
//
//                    pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
//                    ((MainActivity)MainActivity.mContext).getAlarmManager().
//                            setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//                                    Calendar.getInstance().getTimeInMillis()+5000,
//                                    pendingIntent);
//                }
            }
        };

        doStart();
        timer.schedule(timerTask, 1000, 1000); //Timer 실행
//        timer.schedule(timerTask, 300000, 300000); //Timer 실행

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d(TAG, "onCreate: SleepingActivity button clicked");

                doStop();
                timer.cancel();//타이머 종료

                pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
                ((MainActivity)MainActivity.mContext).getAlarmManager().cancel(pendingIntent);
                finish();
            }
        });
    }

    public void DBinit(){
        /************ DB 설정 ************/
        String filename = "UsrData.db";
        try {
            File databseFile = getDatabasePath(filename);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);
        } catch (SQLiteException e) {
            String databasePath = getFilesDir().getPath()+"/"+filename;
            File databaseFile = new File(databasePath);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);

            e.printStackTrace() ;
        }

        String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS " + getString(R.string.TABLE_NAME_TODAY) + " (" +
                getString(R.string.TABLE_COL_TIME) +" TEXT, " +
                getString(R.string.TABLE_COL_LEVEL) +" INTEGER);" ;

        sqliteDB.execSQL(sqlCreateTbl);

        sqlCreateTbl = "CREATE TABLE IF NOT EXISTS " + getString(R.string.TABLE_NAME_YESTERDAY) + " (" +
                getString(R.string.TABLE_COL_TIME) +" TEXT, " +
                getString(R.string.TABLE_COL_LEVEL) +" INTEGER);" ;

        sqliteDB.execSQL(sqlCreateTbl);
    }

    public void TimeTaskStop(){
        timer.cancel();
    }

    public void doStart()
    {
        audioReader.startReader(mSampleRate, inputBlockSize * sampleDecimate, new AudioReader.Listener()
        {
            @Override
            public final void onReadComplete(int dB)
            {
                receiveDecibel(dB);
                if(dB > -200) {
                    sumOfDB += dB;
                    cnt++;
                }
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
}


