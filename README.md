# NOA20_sleeper
NOA 2020 Daejeon Sleeper Project   
수면 패턴 분석을 통한 스마트 무드등
🏅 아랩 스타트업 액셀러레이팅 최우수상

## 설명
어플리케이션을 통해 수면 패턴을 분석하고 분석 결과를 서버와 데이터베이스에 저장한다.   
수면등은 저장한 데이터를 기반으로 실시간 동기화된다.   

## 기능
* 마이크를 통한 사용자의 패턴 분석
* 수면등과 소통
* PHP를 통해 mySQL과 연동하여 사용자 데이터베이스 생성
* 사용자 데이터 시각화

## [Android](https://github.com/eun-seong/NOA20_sleeper/tree/master/NOA20_Sleeper)
안드로이드 어플리케이션   
#### FragmentDaily.java 
소음 측정을 통해 일정 수치 이상/이하일 경우 깊은 수면/얕은 수면/깨어남 상태 구분   
'지난 밤' 탭 주요 코드   
```java
while(cursor.moveToNext()) {
    int level = cursor.getInt(1);
    cnt++;

    if(level<Integer.parseInt(getString(R.string.INT_DEEP)))
	// 깊은 수면
	entries.add(new BarEntry(cnt, new float[]{level, 0, 0}));
    else if(level<Integer.parseInt(getString(R.string.INT_SHALLOW)))
	// 얕은 수면
	entries.add(new BarEntry(cnt, new float[]{0, level, 0}));
    else
	// 깨어난 상태
	entries.add(new BarEntry(cnt, new float[]{0, 0, level}));
}
```
   
#### FragmentStatistics.java
'통계' 탭 주요 코드
```java
// 서버에서 수치를 json 형태로 받아 그래프 시각화
String myJSON = new GetData().execute("getStatisticsData.php").get();
JSONObject jsonObject = new JSONObject(myJSON);
JSONArray dataJSON = jsonObject.getJSONArray(getString(R.string.TABLE_NAME_STATISTICS));
int cnt=0;
for(int I=0;i<4;i++){
    ArrayList<BarEntry> Entries = new ArrayList<>();
    JSONArray c = dataJSON.getJSONObject(i).getJSONArray(COL_NAMES[I]);
    for(int j=0; j <c.length() ; j++){
        cnt++;
        Entries.add(new BarEntry(cnt, c.getInt(j)));
    }
    Chartinit(Entries, i) ;
}

...

// 서버에서 통계 수치를 json 형채로 받아 textView에 입력
String myJSON = new GetData().execute("getStatistics.php").get();
JSONObject jsonObject = new JSONObject(myJSON);
JSONArray dataJSON = jsonObject.getJSONArray(getString(R.string.TABLE_NAME_STATISTICS));

JSONObject c = dataJSON.getJSONObject(0).getJSONObject(COL_NAMES[0]);
// 단위가 %이기 때문에 수면의 질만 따로 처리
tv[0].setText(String.format("%s%%", c.getString(getString(R.string.MEAN))));
tv[1].setText(String.format("%s%%", c.getString(getString(R.string.MAX))));
tv[2].setText(String.format("%s%%", c.getString(getString(R.string.MIN))));

for(int i=1;i<4;i++){
    c = dataJSON.getJSONObject(i).getJSONObject(COL_NAMES[i]);
    tv[i * 3].setText((CalculateTime.calculate(c.getInt(getString(R.string.MEAN)))));
    tv[i * 3 + 1].setText((CalculateTime.calculate(c.getInt(getString(R.string.MAX)))));
    tv[i * 3 + 2].setText((CalculateTime.calculate(c.getInt(getString(R.string.MIN)))));
}
```

#### SleepingActivity.java
알람 설정한 시각의 30분 전 && 소음이 깨어남 수준 이면 알람을 울림(사용자의 수면 패턴에 맞춰)    
주요 코드   
```java
if(setTime-1800000 < now && level>Integer.parseInt(getString(R.string.INT_SHALLOW))){
    pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
    ((MainActivity)MainActivity.mContext).getAlarmManager().
            setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    Calendar.getInstance().getTimeInMillis()+5000,
                    pendingIntent);
}
```


## Server
### MySQL
서버에 존재하는 데이터베이스
#### TABLE | usr_statistics
사용자의 일주일 통계가 저장되어 있다.   
각 열은 날짜, 총 수면 시간, 수면의 질, 깨어 있던 시간, 얕은 수면, 깊은 수면, 취침 시간, 기상 시간을 의미한다.
```sql
mysql> desc usr_statistics;
+--------------+----------+------+-----+-------------------+-----------------------------------------------+
| Field        | Type     | Null | Key | Default           | Extra                                         |
+--------------+----------+------+-----+-------------------+-----------------------------------------------+
| date         | datetime | YES  |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED on update CURRENT_TIMESTAMP |
| totalTime    | int      | YES  |     | NULL              |                                               |
| quality      | int      | YES  |     | NULL              |                                               |
| awakeTime    | int      | YES  |     | NULL              |                                               |
| shallowSleep | int      | YES  |     | NULL              |                                               |
| deepSleep    | int      | YES  |     | NULL              |                                               |
| bedTime      | int      | YES  |     | NULL              |                                               |
| getupTime    | int      | YES  |     | NULL              |                                               |
+--------------+----------+------+-----+-------------------+-----------------------------------------------+

```

#### TABLE | ino_data
아두이노로 보낼 데이터가 담겨 있다.    
각 열은 알람시각, 총 수면 시간, 얕은 수면, 깊은 수면을 뜻한다.
```sql
mysql> desc ino_data;
+--------------+------+------+-----+---------+-------+
| Field        | Type | Null | Key | Default | Extra |
+--------------+------+------+-----+---------+-------+
| alarmTime    | int  | YES  |     | NULL    |       |
| totalTime    | int  | YES  |     | NULL    |       |
| shallowSleep | int  | YES  |     | NULL    |       |
| deepSleep    | int  | YES  |     | NULL    |       |
+--------------+------+------+-----+---------+-------+
```

### [PHP](https://github.com/eun-seong/NOA20_sleeper/tree/master/PHP)
서버에서 실행되는 php 파일
#### inoGetData.php
아두이노에 데이터를 json 형식으로 보낸다.   
주요 코드
```php
    while ($row = ($stmt->fetch(PDO::FETCH_ASSOC))) {
        extract($row);
        array_push(
            $data,
            array(
                'alarmTime' => $alarmTime,
                'totalTime' => $totalTime,
                'shallowSleep' => $shallowSleep,
                'deepSleep' => $deepSleep
            )
        );
    }
    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("usr_statistics"=>$data), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
```

* * *
### 참조한 사이트
차트 API : [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart), [JavaDocs](https://javadoc.jitpack.io/com/github/PhilJay/MPAndroidChart/v3.1.0/javadoc/)   
알람-액티비티 : [조길상의 블로그](https://m.blog.naver.com/PostView.nhn?blogId=jogilsang&logNo=221513058119&proxyReferer=https%3A%2F%2Fwww.google.com%2F)   
Timepicker-알람 : [멈춘보단 천천히라도](https://webnautes.tistory.com/1365)   
SharedPreference Class : [숲속의 작은 이야기](https://re-build.tistory.com/37)   
데시벨 구하기 : [쎄미](https://susemi99.tistory.com/1017)   
http통신 : [webnautes](https://webnautes.tistory.com/1189)    
