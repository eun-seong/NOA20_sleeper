package com.example.noa20_sleeper.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noa20_sleeper.CalculateTime;
import com.example.noa20_sleeper.GetData;
import com.example.noa20_sleeper.R;

import org.json.JSONArray;
import org.json.JSONObject;

// 네비게이션에서 통계 탭
public class FragmentStatistic extends Fragment {
    private static final String TAG = "LOG_TAG";
    private static final String TAG_DATA = "data";
    private static final String TAG_MEAN = "mean";
    private static final String TAG_BEST = "best";
    private static final String TAG_WORST = "worst";

    private TextView[] tv;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        Log.d(TAG, "onCreateView: FragmentStatistics created");

        tv = new TextView[12];
        int[] tvId = {
                R.id.statistic_quality_mean, R.id.statistic_quality_best, R.id.statistic_quality_worst,
                R.id.statistic_total_mean, R.id.statistic_total_best, R.id.statistic_total_worst,
                R.id.statistic_bed_mean, R.id.statistic_bed_best, R.id.statistic_bed_worst,
                R.id.statistic_getup_mean, R.id.statistic_getup_best, R.id.statistic_getup_worst
        };

        for(int i=0;i<12;i++){
            tv[i] = view.findViewById(tvId[i]);
        }

        // TODO 그래프 그리기

        try {
            String myJSON = new GetData().execute("getData.php").get();
            JSONObject jsonObject = new JSONObject(myJSON);
            JSONArray dataJSON = jsonObject.getJSONArray(TAG_DATA);

            JSONObject c = dataJSON.getJSONObject(0);
            tv[0].setText(c.getString(TAG_MEAN)+"%");
            tv[1].setText(c.getString(TAG_BEST)+"%");
            tv[2].setText(c.getString(TAG_WORST)+"%");

            int num = dataJSON.length();
            for(int i=1;i<num;i++){
                c = dataJSON.getJSONObject(i);
                tv[i * 3].setText((CalculateTime.calculate(c.getString(TAG_MEAN))));
                tv[i * 3 + 1].setText((CalculateTime.calculate(c.getString(TAG_BEST))));
                tv[i * 3 + 2].setText((CalculateTime.calculate(c.getString(TAG_WORST))));
            }

        } catch (Exception e){
            Log.e(TAG, "onCreateView: FragmentDaily ", e);
        }

        return view;
    }
}