package com.example.noa20_sleeper.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.noa20_sleeper.Fragment.FragmentAlarm;
import com.example.noa20_sleeper.Fragment.FragmentDaily;
import com.example.noa20_sleeper.Fragment.FragmentSetting;
import com.example.noa20_sleeper.Fragment.FragmentStatistic;
import com.example.noa20_sleeper.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LOG_TAG";
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentAlarm fragmentAlarm = new FragmentAlarm();
    private FragmentDaily fragmentDaily = new FragmentDaily();
    private FragmentSetting fragmentSetting = new FragmentSetting();
    private FragmentStatistic fragmentStatistic = new FragmentStatistic();
    private static AlarmManager alarmManager;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionCheck();

        Log.d(TAG, "onCreate: MainActivty created");
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mContext = this;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentAlarm).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    // 네비게이션 바 설정
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.Alarm:
                    transaction.replace(R.id.frameLayout, fragmentAlarm).commitAllowingStateLoss();
                    break;
                case R.id.Daily:
                    transaction.replace(R.id.frameLayout, fragmentDaily).commitAllowingStateLoss();
                    break;
                case R.id.Setting:
                    transaction.replace(R.id.frameLayout, fragmentSetting).commitAllowingStateLoss();
                    break;
                case R.id.Statistic:
                    transaction.replace(R.id.frameLayout, fragmentStatistic).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    public AlarmManager getAlarmManager(){
        return alarmManager;
    }

    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
}