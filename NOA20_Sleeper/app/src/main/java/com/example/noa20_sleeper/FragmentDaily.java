package com.example.noa20_sleeper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

// 네비게이션에서 지난 밤 탭
public class FragmentDaily extends Fragment {
    private static final String TAG = "LOG_TAG";
    private static final String nextNotifyTime_key="nextNotifyTime";
    private int[] colorClassArray;
    private BarChart dailyChart;
    private Context mContext;
    private SQLiteDatabase sqliteDB = null ;
    private ArrayList<BarEntry> entries;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        Log.d(TAG, "onCreateView: FragmentDaily created");
        mContext = this.getContext();

        colorClassArray = new int[]{
                ContextCompat.getColor(mContext,R.color.YELLOW),
                ContextCompat.getColor(mContext,R.color.GREEN),
                ContextCompat.getColor(mContext,R.color.BLUE)};
        TextView[] textViews = new TextView[]{
                view.findViewById(R.id.date),
                view.findViewById(R.id.day),
                view.findViewById(R.id.daily_totalTime),
                view.findViewById(R.id.daily_quality),
                view.findViewById(R.id.daily_awakeTime),
                view.findViewById(R.id.daily_shallowSleep),
                view.findViewById(R.id.daily_deepSleep),
                view.findViewById(R.id.daily_bedTime),
                view.findViewById(R.id.daily_getupTime)};
        dailyChart = view.findViewById(R.id.daily_chart);
        entries = new ArrayList<>();

        long millis = PreferenceManager.getLong(mContext,nextNotifyTime_key);
        if(millis == -1) millis = Calendar.getInstance().getTimeInMillis();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);

        DBinit();
        String sqlSelect = "SELECT * FROM " + getString(R.string.TABLE_NAME_YESTERDAY);
        Cursor cursor = sqliteDB.rawQuery(sqlSelect, null);

        int cnt = 0;
        while(cursor.moveToNext()) {
            int level = cursor.getInt(1);
            cnt++;

            if(level<Integer.parseInt(getString(R.string.INT_DEEP))) entries.add(new BarEntry(cnt, new float[]{level, 0, 0}));
            else if(level<Integer.parseInt(getString(R.string.INT_SHALLOW))) entries.add(new BarEntry(cnt, new float[]{0, level, 0}));
            else entries.add(new BarEntry(cnt, new float[]{0, 0, level}));
        }

        try {
            String myJSON = new GetData().execute("getYesterday.php").get();
            JSONObject jsonObject = new JSONObject(myJSON);
            JSONObject yesterdayJSON = jsonObject.getJSONArray(getString(R.string.TABLE_NAME_STATISTICS)).getJSONObject(0);
            Log.d(TAG, "onCreateView: JSON "+yesterdayJSON);

            textViews[2].setText((CalculateTime.calculate(yesterdayJSON.getInt(getString(R.string.COL_TOTALTIME)))));
            textViews[3].setText(String.format("%s%%", Integer.toString(yesterdayJSON.getInt(getString(R.string.COL_QUALITY)))));
            textViews[4].setText((CalculateTime.calculate(yesterdayJSON.getInt(getString(R.string.COL_AWAKETIME)))));
            textViews[5].setText((CalculateTime.calculate(yesterdayJSON.getInt(getString(R.string.COL_SHALLOWSLEEP)))));
            textViews[6].setText((CalculateTime.calculate(yesterdayJSON.getInt(getString(R.string.COL_DEEPSLEEP)))));
            textViews[7].setText((CalculateTime.calculate(yesterdayJSON.getInt(getString(R.string.COL_BEDTIME)))));
            textViews[8].setText((CalculateTime.calculate(yesterdayJSON.getInt(getString(R.string.COL_GETUPTIME)))));
        } catch (Exception e){
            Log.e(TAG, "onCreateView: FragmentDaily ", e);
        }
        textViews[0].setText(new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(calendar.getTime()));
        textViews[1].setText(new SimpleDateFormat("EE요일", Locale.getDefault()).format(calendar.getTime()));

        Chartinit();
        return view;
    }

    private void Chartinit(){
        int [] colorClassArray = new int[]{Color.CYAN , Color.GREEN, Color.YELLOW};
        XAxis xAxis = dailyChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Description description = new Description();
        description.setText("");
        BarDataSet barDataSet = new BarDataSet(entries, "");
        BarData barData = new BarData(barDataSet);
        barDataSet.setColors(colorClassArray);
        dailyChart.getAxisLeft().setEnabled(false);
        dailyChart.setDescription(description);
        dailyChart.getXAxis().setEnabled(false);
        dailyChart.getLegend().setEnabled(false);
        dailyChart.getAxisRight().setEnabled(false);
        dailyChart.setFitBars(true);
        dailyChart.animateXY(6, 6);
        dailyChart.setPinchZoom(false);
        dailyChart.setTouchEnabled(false);
        dailyChart.setDoubleTapToZoomEnabled(false);
        dailyChart.setData(barData);
        dailyChart.invalidate();
        YAxis leftAxis = dailyChart.getAxisLeft();
        YAxis rightAxis = dailyChart.getAxisRight();
        leftAxis.setAxisMinValue(0f);
        rightAxis.setAxisMinValue(0f);
    }

    private void DBinit(){
        /************ DB 설정 ************/
        String filename = "UsrData.db";
        try {
            File databseFile = mContext.getDatabasePath(filename);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);
        } catch (SQLiteException e) {
            String databasePath = mContext.getFilesDir().getPath()+"/"+filename;
            File databaseFile = new File(databasePath);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
            e.printStackTrace() ;
        }
        String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS " + getString(R.string.TABLE_NAME_YESTERDAY) + " (" +
                getString(R.string.TABLE_COL_TIME) +" TEXT, " +
                getString(R.string.TABLE_COL_LEVEL) +" INTEGER);" ;

        sqliteDB.execSQL(sqlCreateTbl);
    }
}