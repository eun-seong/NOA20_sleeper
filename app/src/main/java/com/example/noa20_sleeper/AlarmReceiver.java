package com.example.noa20_sleeper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.PowerManager;



// 시간에 맞춰 알람을 울리게 하는 리시버
public class AlarmReceiver extends BroadcastReceiver {
    Context context;

    //PowerManager.WakeLock 빈객체 선언한다.
    private static PowerManager.WakeLock mCpuWakeLock;
    private static ConnectivityManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        WakeLockUtil.acquireCpuWakeLock(this.context, mCpuWakeLock);

        manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

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
        if (mCpuWakeLock != null) {
            mCpuWakeLock.release();
            mCpuWakeLock = null;
        }
    }

}