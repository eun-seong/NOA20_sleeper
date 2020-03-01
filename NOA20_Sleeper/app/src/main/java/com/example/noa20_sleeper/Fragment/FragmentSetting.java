package com.example.noa20_sleeper.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noa20_sleeper.R;

// 네비게이션에서 설정 탭
public class FragmentSetting extends Fragment {
    private static final String TAG = "LOG_TAG";
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        Log.d(TAG, "onCreateView: FragmentSetting created");

        return view;
}
}