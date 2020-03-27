package com.example.noa20_sleeper.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noa20_sleeper.PreferenceManager;
import com.example.noa20_sleeper.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

// 네비게이션에서 지난 밤 탭
public class FragmentDaily extends Fragment {
    private static final String TAG = "LOG_TAG";
    private static final String nextNotifyTime_key="nextNotifyTime";
    private static final String TAG_YESTERDAY = "yesterday";
    private static final String TAG_TOTALTIME = "totalTime";
    private static final String TAG_QUALITY = "quality";
    private static final String TAG_AWAKETIME = "awakeTime";
    private static final String TAG_SHALLOWSLEEP = "shallowSleep";
    private static final String TAG_DEEPSLEEP = "deepSleep";
    private static final String TAG_BEDTIME = "bedTime";
    private static final String TAG_GETUPTIME = "getupTime";

    private BarChart dailyChart;
    private Context mContext;
    private SQLiteDatabase sqliteDB = null ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        Log.d(TAG, "onCreateView: FragmentDaily created");

        mContext = this.getContext();

        ArrayList<BarEntry> entries = new ArrayList<>();
        TextView tv_date = view.findViewById(R.id.date);
        TextView tv_day = view.findViewById(R.id.day);
        TextView tv_totalTime = view.findViewById(R.id.daily_totalTime);
        TextView tv_quality = view.findViewById(R.id.daily_quality);
        TextView tv_awakeTime = view.findViewById(R.id.daily_awakeTime);
        TextView tv_shallowSleep = view.findViewById(R.id.daily_shallowSleep);
        TextView tv_deepSleep = view.findViewById(R.id.daily_deepSleep);
        TextView tv_bedTime = view.findViewById(R.id.daily_bedTime);
        TextView tv_getupTime = view.findViewById(R.id.daily_getupTime);
        dailyChart = view.findViewById(R.id.daily_chart);

        XAxis xAxis = dailyChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        final String[] time = new String[]{ "11pm", "12am", "1am", "2am", "3am","4am", "5am", "6am", "7am"};
//        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(time);
//        xAxis.setValueFormatter(formatter);
//        xAxis.setGranularity(0.1f);
        dailyChart.setFitBars(true);
        dailyChart.animateXY(6, 6);

        long millis = PreferenceManager.getLong(mContext,nextNotifyTime_key);
        if(millis == -1) millis = Calendar.getInstance().getTimeInMillis();

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);

        Date yesterday = calendar.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(yesterday);
        String day_text = new SimpleDateFormat("EE요일", Locale.getDefault()).format(yesterday);

        tv_date.setText(date_text);
        tv_day.setText(day_text);
//
//        try {
//            String myJSON = new GetData().execute("getYesterday.php").get();
//            JSONObject jsonObject = new JSONObject(myJSON);
//            JSONObject yesterdayJSON = jsonObject.getJSONArray(TAG_YESTERDAY).getJSONObject(0);
//
//            tv_totalTime.setText((CalculateTime.calculate(yesterdayJSON.getString(TAG_TOTALTIME))));
//            tv_quality.setText(yesterdayJSON.getString(TAG_QUALITY)+"%");
//            tv_awakeTime.setText((CalculateTime.calculate(yesterdayJSON.getString(TAG_AWAKETIME))));
//            tv_shallowSleep.setText((CalculateTime.calculate(yesterdayJSON.getString(TAG_SHALLOWSLEEP))));
//            tv_deepSleep.setText((CalculateTime.calculate(yesterdayJSON.getString(TAG_DEEPSLEEP))));
//            tv_bedTime.setText((CalculateTime.calculate(yesterdayJSON.getString(TAG_BEDTIME))));
//            tv_getupTime.setText((CalculateTime.calculate(yesterdayJSON.getString(TAG_GETUPTIME))));
//
//        } catch (Exception e){
//            Log.e(TAG, "onCreateView: FragmentDaily ", e);
//        }

        DBinit();

        String sqlSelect = "SELECT * FROM " + getString(R.string.TABLE_NAME_YESTERDAY);
        Cursor cursor = sqliteDB.rawQuery(sqlSelect, null);

        int cnt = 0, max = 0, min = 987654321;
        while(cursor.moveToNext()) {
            String time = cursor.getString(0);
            int level = cursor.getInt(1);
            entries.add(new BarEntry(cnt, level));
            cnt++;

            int hour = Integer.parseInt(time.substring(0,2));
            int minute = Integer.parseInt(time.substring(2));



            // TODO 제일 최신의 csv 파일을 가져와서 읽기
            // TODO 그래프 그리기

        }

        BarDataSet barDataSet = new BarDataSet(entries, "dB");
        barDataSet.setBarBorderWidth(0.001f);
        barDataSet.setColor(0xFF00BFFF);
        BarData barData = new BarData(barDataSet);
        dailyChart.setPinchZoom(false);
        dailyChart.setTouchEnabled(false);
        dailyChart.setDoubleTapToZoomEnabled(false);
        dailyChart.setData(barData);
        dailyChart.invalidate();

        return view;
    }
    public void DBinit(){
        /************ DB 설정 ************/
        String filename = "UsrData.db";
        try {
            File databseFile = mContext.getDatabasePath(filename);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);
//            sqliteDB = SQLiteDatabase.openOrCreateDatabase("Widgets.db", null) ;
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