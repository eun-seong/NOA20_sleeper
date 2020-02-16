package com.example.noa20_sleeper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;


import static android.content.Context.WIFI_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;

    //PowerManager.WakeLock 빈객체 선언한다.
    private static PowerManager.WakeLock sCpuWakeLock;
    private static WifiManager.WifiLock sWifiLock;
    private static ConnectivityManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (sCpuWakeLock != null) {
            return;
        }

        if (sWifiLock != null) {
            return;
        }

        // 절전모드로 와이파이 꺼지는것을 방지
        WifiManager wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        sWifiLock = wifiManager.createWifiLock("wifilock");
        sWifiLock.setReferenceCounted(true);
        sWifiLock.acquire();

        // 시스템에서 powermanager 받아옴
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        // 객체의 제어레벨 설정
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "app:alarm");

        //acquire 함수를 실행하여 앱을 깨운다. cpu 를 획득한다
        sCpuWakeLock.acquire();


        manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        this.context = context;

        // 작동할 액티비티를 설정한다
        Intent alarmIntent = new Intent("android.intent.action.sec");

        alarmIntent.setClass(context, AlarmActivity.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        SleepingActivity sleepingActivity = (SleepingActivity)SleepingActivity._SleepingActivity;
        sleepingActivity.finish();

        // 액티비티를 띄운다
        this.context.startActivity(alarmIntent);


        // acquire 함수를 사용하였으면 꼭 release 를 해주어야 한다.
        // cpu를 점유하게 되어 배터리 소모나 메모리 소모에 영향을 미칠 수 있다
        if(sWifiLock != null) {
            sWifiLock.release();
            sWifiLock = null;
        }

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}