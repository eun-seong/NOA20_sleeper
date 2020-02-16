package com.example.noa20_sleeper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// 사용자가 알람을 설정하고 잠에 들었을 때 나오는 화면
public class SleepingActivity extends AppCompatActivity {
    public static Activity _SleepingActivity;

    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeping);
        _SleepingActivity = SleepingActivity.this;

        cancelButton = findViewById(R.id.bt_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // TODO 알람 기능 끄기
                finish();
            }
        });

    }
}
