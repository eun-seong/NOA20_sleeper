package com.example.noa20_sleeper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioAttributes;
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

import java.io.File;

// 알람이 울릴 때 화면
public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "LOG_TAG";
    private Button wakeupButton;
    private Vibrator vibrator;
    private Uri uri;
    private Ringtone ringtone;
    private AudioAttributes audioAttributes;
    private SQLiteDatabase sqliteDB = null ;

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

        DBinit();
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

    public void DBinit(){
        /************ DB 설정 ************/
        String filename = "UsrData.db";
        try {
            File databseFile = getDatabasePath(filename);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);
//            sqliteDB = SQLiteDatabase.openOrCreateDatabase("Widgets.db", null) ;
        } catch (SQLiteException e) {
            String databasePath = getFilesDir().getPath()+"/"+filename;
            File databaseFile = new File(databasePath);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);

            e.printStackTrace();
        }

        String sql = "SELECT * FROM " + getString(R.string.TABLE_NAME_TODAY);
        Cursor cursor = sqliteDB.rawQuery(sql, null);

        int count = cursor.getCount();
        if(count>0){
            sql= "DELETE FROM " + getString(R.string.TABLE_NAME_YESTERDAY);
            sqliteDB.execSQL(sql);
            Log.d(TAG, "DBinit: DELETE YESTERDAY");
            sql = "INSERT INTO "+getString(R.string.TABLE_NAME_YESTERDAY)+" SELECT * FROM "+getString(R.string.TABLE_NAME_TODAY);
            sqliteDB.execSQL(sql);
            Log.d(TAG, "DBinit: COPY TODAY");
            sql= "DELETE FROM " + getString(R.string.TABLE_NAME_TODAY);
            sqliteDB.execSQL(sql);
            Log.d(TAG, "DBinit: DELETE TODAY");
        }
        cursor.close();

        sql = "SELECT * FROM " + getString(R.string.TABLE_NAME_YESTERDAY);
        cursor = sqliteDB.rawQuery(sql, null);

        int cnt = 0, num = cursor.getCount(), getupTime=0, bedTime=0, totalTime=num*5, deep = 0, shallow = 0, wakeup = 0;
        Log.d(TAG, "DBinit: cursorNum : "+num);

        while(cursor.moveToNext()) {
            String time = cursor.getString(0);
            int level = cursor.getInt(1);
            Log.d(TAG, "DBinit:\ttime: "+time+"\tlevel: "+level);
            cnt++;

            if(level<Integer.parseInt(getString(R.string.INT_DEEP))) deep++;
            else if(level<Integer.parseInt(getString(R.string.INT_SHALLOW))) shallow++;
            else wakeup++;

            int hour = Integer.parseInt(time.substring(0,2));
            int minute = Integer.parseInt(time.substring(3));

            if(cnt==1) bedTime = hour*60 + minute;
            else if(cnt==num) getupTime = hour*60 + minute;
            Log.d(TAG, "DBinit:\tcnt: "+cnt);
        }
        cursor.close();

        long quality = Math.round((double)(deep+shallow)/(double)(deep+shallow+wakeup)*100);
        InsertData task = new InsertData();
        task.execute("addData.php",
                getString(R.string.COL_TOTALTIME), Integer.toString(totalTime),
                getString(R.string.COL_BEDTIME), Integer.toString(bedTime),
                getString(R.string.COL_GETUPTIME), Integer.toString(getupTime),
                getString(R.string.COL_QUALITY), Long.toString(quality),/**/
                getString(R.string.COL_AWAKETIME), Integer.toString(wakeup*5),
                getString(R.string.COL_SHALLOWSLEEP), Integer.toString(shallow*5),
                getString(R.string.COL_DEEPSLEEP), Integer.toString(deep*5));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}



















