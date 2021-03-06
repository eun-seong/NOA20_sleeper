package com.example.noa20_sleeper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

// 네비게이션에서 통계 탭
public class FragmentStatistic extends Fragment {
    private static final String TAG = "LOG_TAG";
    private TextView[] tv;
    private String[] COL_NAMES;
    private int[] tvId;
    private BarChart[] Charts;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        Log.d(TAG, "onCreateView: FragmentStatistics created");
        initValue(view);
        setNums(view);

        Charts = new BarChart[]{
                view.findViewById(R.id.quality_chart),
                view.findViewById(R.id.time_chart),
                view.findViewById(R.id.gotobed_chart),
                view.findViewById(R.id.wakeup_chart)};
        try {
            String myJSON = new GetData().execute("getStatisticsData.php").get();
            JSONObject jsonObject = new JSONObject(myJSON);
            JSONArray dataJSON = jsonObject.getJSONArray(getString(R.string.TABLE_NAME_STATISTICS));
            int cnt=0;
            for(int i=0;i<4;i++){
                ArrayList<BarEntry> Entries = new ArrayList<>();
                JSONArray c = dataJSON.getJSONObject(i).getJSONArray(COL_NAMES[i]);
                Log.d(TAG, "onCreateView: JSON"+c);
                for(int j=0; j <c.length() ; j++){
                    cnt++;
                    Entries.add(new BarEntry(cnt, c.getInt(j)));
                }
                Chartinit(Entries, i) ;
            }

        } catch (Exception e){
            Log.e(TAG, "onCreateView: FragmentDaily ", e);
        }
        return view;
    }

    private void Chartinit(ArrayList<BarEntry> Entries, int i) {
        XAxis xAxis = Charts[i].getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Description description = new Description();
        description.setText("");
        BarDataSet barDataSet = new BarDataSet(Entries,"");
        BarData barData = new BarData(barDataSet);
        barDataSet.setColor(0xFF87CEEB);
        Charts[i].getAxisLeft().setEnabled(false);
        Charts[i].setDescription(description);
        Charts[i].getXAxis().setEnabled(false);
        Charts[i].getLegend().setEnabled(false);
        Charts[i].getAxisRight().setEnabled(false);
        Charts[i].setFitBars(true);
        Charts[i].animateXY(6, 6);
        Charts[i].setPinchZoom(false);
        Charts[i].setTouchEnabled(false);
        Charts[i].setDoubleTapToZoomEnabled(false);
        Charts[i].setData(barData);
        Charts[i].invalidate();
    }


    private void initValue(View view){
        tv = new TextView[12];
        tvId = new int[]{
                R.id.statistic_quality_mean, R.id.statistic_quality_best, R.id.statistic_quality_worst,
                R.id.statistic_total_mean, R.id.statistic_total_best, R.id.statistic_total_worst,
                R.id.statistic_bed_mean, R.id.statistic_bed_best, R.id.statistic_bed_worst,
                R.id.statistic_getup_mean, R.id.statistic_getup_best, R.id.statistic_getup_worst
        };
        COL_NAMES = new String[]{
                getString(R.string.COL_QUALITY),
                getString(R.string.COL_TOTALTIME),
                getString(R.string.COL_BEDTIME),
                getString(R.string.COL_GETUPTIME)
        };

        for(int i=0;i<12;i++){
            tv[i] = view.findViewById(tvId[i]);
        }
    }

    private void setNums(View view){

        try {
            String myJSON = new GetData().execute("getStatistics.php").get();
            JSONObject jsonObject = new JSONObject(myJSON);
            JSONArray dataJSON = jsonObject.getJSONArray(getString(R.string.TABLE_NAME_STATISTICS));

            JSONObject c = dataJSON.getJSONObject(0).getJSONObject(COL_NAMES[0]);
            Log.d(TAG, "onCreateView: JSON "+c);
            tv[0].setText(String.format("%s%%", c.getString(getString(R.string.MEAN))));
            tv[1].setText(String.format("%s%%", c.getString(getString(R.string.MAX))));
            tv[2].setText(String.format("%s%%", c.getString(getString(R.string.MIN))));

            for(int i=1;i<4;i++){
                c = dataJSON.getJSONObject(i).getJSONObject(COL_NAMES[i]);
                tv[i * 3].setText((CalculateTime.calculate(c.getInt(getString(R.string.MEAN)))));
                tv[i * 3 + 1].setText((CalculateTime.calculate(c.getInt(getString(R.string.MAX)))));
                tv[i * 3 + 2].setText((CalculateTime.calculate(c.getInt(getString(R.string.MIN)))));
            }

        } catch (Exception e){
            Log.e(TAG, "onCreateView: FragmentDaily ", e);
        }
    }
}