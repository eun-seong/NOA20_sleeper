# 수면 패턴 분석 App과 스마트 무드등 [Sleeper]
## 프로젝트 요약
> 2020.02. - 2020.08.   

> Android Application Developer      
> Back-End Developer   
* 🏅 교내 아랩 스타트업 액셀러레이팅 최우수상
* 불면증 개선을 위해 기획하였습니다.
* 모바일 앱을 통해 수면 패턴을 분석하고 분석 결과를 시각화합니다.
* 앱과 연결된 무드등은 수면 패턴에 따라 실시간으로 동기화되어 on/off 됩니다.

## 담당 역할
### FE
![Langauge:Java](https://img.shields.io/badge/Langauge-Java-green) ![platform:Android](https://img.shields.io/badge/Platform-Android-blue)

* UI/UX Design
* 마이크를 통한 사용자의 수면 패턴 분석
* 무드등의 NodeMCU와 통신
* 사용자 데이터 시각화

### BE
![Langauge:Java](https://img.shields.io/badge/Langauge-PHP-green) ![platform:Android](https://img.shields.io/badge/Platform-AWS-blue) ![Langauge:Java](https://img.shields.io/badge/DB-MySQL-yellow)

* AWS free tier 사용
* MySQL 사용자 DB 생성
* 무드등의 NodeMCU가 요청한 request를 json으로 응답
* 무드등에서 표시할 날씨 및 기상 정보 [OpenWeather](https://openweathermap.org/api) API 사용


* * *
### 참조한 사이트
차트 API : [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart), [JavaDocs](https://javadoc.jitpack.io/com/github/PhilJay/MPAndroidChart/v3.1.0/javadoc/)   
알람-액티비티 : [조길상의 블로그](https://m.blog.naver.com/PostView.nhn?blogId=jogilsang&logNo=221513058119&proxyReferer=https%3A%2F%2Fwww.google.com%2F)   
Timepicker-알람 : [멈춘보단 천천히라도](https://webnautes.tistory.com/1365)   
SharedPreference Class : [숲속의 작은 이야기](https://re-build.tistory.com/37)   
데시벨 구하기 : [쎄미](https://susemi99.tistory.com/1017)   
http통신 : [webnautes](https://webnautes.tistory.com/1189)    
