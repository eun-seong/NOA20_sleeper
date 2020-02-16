package com.example.noa20_sleeper;

import android.content.Context;
import android.os.PowerManager;

class WakeLockUtil {
    private static final String WAKELOCK_TAG = "app:sleeper";

    static void acquireCpuWakeLock(Context context, PowerManager.WakeLock mCpuWakeLock) {
        if(mCpuWakeLock != null) return;

        // 시스템에서 powermanager 받아옴
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);
        //acquire 함수를 실행하여 앱을 깨운다. cpu 를 획득한다
        mCpuWakeLock.acquire();
    }
}
