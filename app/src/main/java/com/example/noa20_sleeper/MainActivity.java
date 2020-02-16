package com.example.noa20_sleeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentAlarm fragmentAlarm = new FragmentAlarm();
    private FragmentDaily fragmentDaily = new FragmentDaily();
    private FragmentSetting fragmentSetting = new FragmentSetting();
    private FragmentStatistic fragmentStatistic = new FragmentStatistic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}