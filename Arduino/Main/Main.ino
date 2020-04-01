#include <core_build_options.h>
#include <swRTC.h>
#include <Adafruit_GFX.h>
#include <Adafruit_TFTLCD.h>
#include <Adafruit_NeoPixel.h>
#include <Fonts/FreeSansBold24pt7b.h>
#include <TouchScreen.h>
#include "DHT.h"
#include <Wire.h>
#include <DFPlayer_Mini_Mp3.h>
#include <SoftwareSerial.h>


#define RX 50
#define TX 51
#define LCD_CS A3
#define LCD_CD A2
#define LCD_WR A1
#define LCD_RD A0
#define LCD_RESET A4
#define TS_MINX 120
#define TS_MAXX 900
#define TS_MINY 70
#define TS_MAXY 920
#define NEOPIXELPIN 23
#define DHTPIN 22
#define DHTTYPE DHT22
#define NUM_LEDS 70
#define BLACK 0x0000
#define BLUE 0x001F
#define RED 0xF800
#define GREEN 0x07E0
#define CYAN 0x07FF
#define MAGENTA 0xF81F
#define YELLOW 0xFFE0
#define WHITE 0xFFFF
#define YP A3
#define XM A2
#define YM 9
#define XP 8
#define SENSITIVITY 300
#define MINPRESSURE 10
#define MAXPRESSURE 1000
#if defined(__SAM3X8E__)
#undef __FlashStringHelper::F(string_literal)
#define F(string_literal) string_literal
#endif

int buttonPin = 26;
bool buttonState = false;
int button = 0;
bool flag = false;
int tftColours[] = {BLUE, RED, GREEN, CYAN, MAGENTA, YELLOW, WHITE};
float h  ;
float t  ;
int value_bar = 0;
int screen_id = 0;
int r = 0;
int g = 0;
int b = 0;
String at;
int atime;
String total;
String shallow;
String deep;
String nt;
int ntime;
String any;
bool OnOff = false;

Adafruit_TFTLCD tft(LCD_CS, LCD_CD, LCD_WR, LCD_RD, LCD_RESET);
Adafruit_NeoPixel strip = Adafruit_NeoPixel(102, NEOPIXELPIN, NEO_GRB + NEO_KHZ800); //네오픽셀 핀세팅
DHT dht(DHTPIN, DHTTYPE);//DHT
SoftwareSerial mpSerial(14, 15); // RX, TX //MP3 핀세팅
//SoftwareSerial mySerial(51,50);
SoftwareSerial dataSerial(RX, TX);
TouchScreen ts = TouchScreen(XP, YP, XM, YM, SENSITIVITY);


int startlight() { //알람시간 1시간전부터 무드등을 서서히 밝게해주는 함수
  int ah = atime / 60;
  int am = atime - ah * 60;
  int nh = ntime / 60;
  int nm = ntime - nh * 60;
  int aah = ah - 1;
  Serial.print(ah);
  Serial.print(",");
  Serial.print(am);
  Serial.print(",");
  Serial.print(nh);
  Serial.print(",");
  Serial.print(nm);

  Serial.println("데이터 확인");
  if (aah < 0) {
    ah = 23;
  }
  Serial.print("바뀐aah -");
  Serial.println(aah);
  if (aah == nh && am == nm) {
    Serial.print("ttttt");
    delay(100);
    flag = true; //
  }
  if (ah == nh && am == nm) {
    Serial.print("ffffff");
    strip_color(0, 0, 0);
    delay(100);
    flag = false; //
  }
  if (flag == true) {
    int diff = abs(nm - am);
    int value = map(diff, 0, 60, 1, 40);
    delay(100);
    Serial.print("diff :");
    Serial.print(diff);
    delay(100);
    Serial.print("v :");
    Serial.print(value);
    delay(100);
    Serial.println();
    strip_color(value, value, 0);
  }
  else {
    return 0;
  }
}

void strip_color(int r, int g , int b) {//NEOPIXEL
  for (int i = 0; i < 70; i++) {
    strip.setPixelColor(i, strip.Color(r, g, b));
  }
  strip.show();
}

void homeScreen() {//screen_id= 0
  screen_id = 0;
  gettmphum();
  tft.fillScreen(BLACK);
  tft.fillRoundRect(30, 20, 90, 50, 5, WHITE);
  tft.setFont();
  tft.setTextSize(2);
  tft.setTextColor(BLACK);
  tft.setCursor(50, 30);
  tft.print("SLEEP");
  tft.setCursor(55, 45);
  tft.print("DATA");
  tft.fillRoundRect(30, 90, 90, 50, 5, WHITE);
  tft.setFont();
  tft.setTextSize(2);
  tft.setTextColor(BLACK);
  tft.setCursor(50, 110);
  tft.print("MUSIC");
  tft.fillRoundRect(30, 170, 90, 50, 5, WHITE);
  tft.setFont();
  tft.setTextSize(2);
  tft.setTextColor(BLACK);
  tft.setCursor(50, 190);
  tft.print("COLOR");
  tft.setCursor(180, 20);
  tft.setTextColor(WHITE);
  tft.setTextSize(2);
  tft.println("Temp.");
  tft.setCursor(180, 45);
  tft.setTextColor(YELLOW);
  tft.setTextSize(3);
  tft.print(t);
  tft.setTextColor(WHITE);
  tft.setTextSize(3);
  tft.print("C");
  tft.setCursor(165, 90);
  tft.setTextColor(WHITE);
  tft.setTextSize(2);
  tft.print("Humidity.");
  tft.setCursor(180, 115);
  tft.setTextColor(YELLOW);
  tft.setTextSize(3);
  tft.print(h);
  tft.setTextColor(WHITE);
  tft.setTextSize(3);
  tft.print("%");
  tft.setCursor(180, 170);
  tft.setTextColor(WHITE);
  tft.setTextSize(2);
  tft.print("Time.");
  tft.setCursor(180, 190);
  tft.setTextColor(YELLOW);
  tft.setTextSize(3);
  tft.print(((((ntime / 60) + String(":") + (ntime % 60)))));
}
void backspace() {//뒤로가기 버튼
  tft.fillRoundRect(15, 15, 53, 25, 4, WHITE);
  tft.setFont();
  tft.setTextSize(2);
  tft.setTextColor(BLACK);
  tft.setCursor(20, 20);
  tft.print("BACK");
}
void WhiteScreen() {//screen_id=4
  screen_id = 4;
  tft.fillScreen(BLACK);
}
void SleepDataScreen() {//screen_id =1
  screen_id = 1;
  tft.fillScreen(BLACK);
  backspace();
  tft.setCursor(165, 20);
  tft.setTextColor(WHITE);
  tft.setTextSize(2);
  tft.print("unit:minute");
  tft.setCursor(30, 70);
  tft.setTextColor(WHITE);
  tft.setTextSize(3);
  tft.print("ShallowSleep:" + shallow);
  tft.setCursor(30, 120);
  tft.setTextColor(WHITE);
  tft.setTextSize(3);
  tft.print("DeepSleep:" + deep);
  tft.setCursor(30, 170);
  tft.setTextColor(WHITE);
  tft.setTextSize(3);
  tft.print("TotalSleep:" + total );
}
void onoff() { //onoff 버튼
  tft.fillRect(100, 150, 130, 40, WHITE);
  tft.setCursor(118, 160);
  tft.setTextColor(BLACK);
  tft.setTextSize(3);
  tft.println("on/off");
}
void brightnessScreen() { //screen_id =2
  screen_id = 2;
  tft.fillScreen(BLACK);
  backspace();
  onoff();
  tft.setCursor(125, 50);
  tft.setTextColor(WHITE);
  tft.setTextSize(3);
  tft.print(value_bar);
  tft.println("%");
  tft.fillRect(15 + value_bar * 2.9, 100, 290 - value_bar * 2.9, 30, WHITE);
  tft.fillRect(15, 100, value_bar * 2.9, 30, RED);
}
void musicScreen() {//screen_id =3
  screen_id = 3;
  tft.fillScreen(BLACK);
  tft.setCursor(125, 50);
  tft.setTextColor(WHITE);
  tft.setTextSize(3);
  tft.print("TRACK");
  backspace();
  int k = 20;
  for (int i = 0; i < 5; i++) {
    tft.fillRoundRect(k, 110, 50, 50, 5, WHITE);
    tft.setFont();
    tft.setTextSize(2);
    tft.setTextColor(BLACK);
    tft.setCursor(k + 20, 130);
    tft.print(i + 1);
    k = k + 60;
  }
}
void gettmphum(void) {//온습도 구하는 함수
  h = dht.readHumidity(); //습도값을 읽어옴.
  t = dht.readTemperature();//온도값을 읽어옴
  Serial.print("Humidity: ");
  Serial.print(h);
  Serial.print(" %\t");
  Serial.print("Temperature: ");
  Serial.print(t);
  Serial.println(" *C ");
}
void mp3_output(int volum, int music_index) {//mp3 함수
  mp3_set_volume (0);
  mp3_play (music_index);
  mp3_set_volume (volum);
  mp3_play (music_index);    //0001 파일 플레이
  delay (100);
}
void data() { //nodemcu 통신
  if (dataSerial.available() > 0) {
    nt = dataSerial.readStringUntil('\n');
    ntime = nt.toInt();
    at = dataSerial.readStringUntil('\n');
    atime = at.toInt();
    total = dataSerial.readStringUntil('\n');
    shallow = dataSerial.readStringUntil('\n');
    deep = dataSerial.readStringUntil('\n');
    any = dataSerial.readStringUntil('\n');
    Serial.println(ntime);
    Serial.println(atime);
    Serial.println(total);
    Serial.println(shallow);
    Serial.println(deep);

  }
}

void setup() {

  Serial.begin(9600);
  mpSerial.begin (9600);
  //mySerial.begin(115200);
  dataSerial.begin(9600);
  mp3_set_serial (mpSerial); //set softwareSerial for DFPlayer-mini mp3 module
  delay(1);
  dht.begin();
  tft.reset();
  strip.begin();                            // 네오픽셀
  strip.setPixelColor(1, strip.Color(0, 0, 0)); // 네오픽셀
  strip.show();                             // 네오픽셀
  Wire.begin();
  pinMode(buttonPin, INPUT);


  uint16_t identifier = tft.readID();
  if (identifier == 0x9325) {
    Serial.println(F("Found ILI9325 LCD driver"));
  } else if (identifier == 0x9328) {
    Serial.println(F("Found ILI9328 LCD driver"));
  } else if (identifier == 0x4535) {
    Serial.println(F("Found LGDP4535 LCD driver"));
  } else if (identifier == 0x7575) {
    Serial.println(F("Found HX8347G LCD driver"));
  } else if (identifier == 0x9341) {
    Serial.println(F("Found ILI9341 LCD driver"));
  } else if (identifier == 0x8357) {
    Serial.println(F("Found HX8357D LCD driver"));
  } else if (identifier == 0x0101)
  {
    identifier = 0x9341;
    Serial.println(F("Found 0x9341 LCD driver"));
  } else {
    Serial.print(F("Unknown LCD driver chip: "));
    Serial.println(identifier, HEX);
    Serial.println(F("If using the Elegoo 2.8\" TFT Arduino shield, the line:"));
    Serial.println(F("  #define USE_Elegoo_SHIELD_PINOUT"));
    Serial.println(F("should appear in the library header (Elegoo_TFT.h)."));
    Serial.println(F("If using the breakout board, it should NOT be #defined!"));
    Serial.println(F("Also if using the breakout, double-check that all wiring"));
    Serial.println(F("matches the tutorial."));
    identifier = 0x9341;
  }

  tft.begin(identifier);
  tft.setRotation(3);
  WhiteScreen();
}


void loop() {
  button = digitalRead(buttonPin);
  delay(100);
  if (button == HIGH) {
    Serial.println("in");
    if (buttonState == true) {
      Serial.println("ffff");
      buttonState = false;
    }
    else {
      Serial.println("tttt");
      buttonState = true;
    }
  }
  if (buttonState == false) {
    WhiteScreen();
  } else  {
    homeScreen();
    while (buttonState != false) {
      button = digitalRead(buttonPin);
      delay(100);
      if (button == HIGH) {
        Serial.println("in");
        if (buttonState == true) {
          Serial.println("ffff");
          buttonState = false;
        }
        else {
          Serial.println("tttt");
          buttonState = true;
        }
      }
      TSPoint p = ts.getPoint();
      pinMode(XM, OUTPUT);
      pinMode(YP, OUTPUT);

      if (p.z > MINPRESSURE && p.z < MAXPRESSURE) {

        p.x = map(p.x, TS_MINX, TS_MAXX, tft.width(), 0);
        p.y = (tft.height() - map(p.y, TS_MINY, TS_MAXY, tft.height(), 0));

        Serial.print("("); Serial.print(p.x);
        Serial.print(", "); Serial.print(p.y);
        Serial.println(")");
      }
      data();
      gettmphum();
      startlight();
      //
      if (screen_id == 0) {//homescreen 상태일때
        if (p.x > 225 && p.y < 90) {
          SleepDataScreen();
        }
        if (p.x > 110 && p.x < 225 && p.y < 90) {
          musicScreen();
        }
        if (p.x < 110 && p.y < 90) {
          brightnessScreen();
        }
      }

      else if (screen_id == 1) {//sleepdatascreen 상태일때
        if (p.x > 260 && p.y < 60) {
          homeScreen();
        }
      }

      else if (screen_id == 2) { // brightness 상태일때
        if (p.x > 260 && p.y < 60) {
          homeScreen();
        }
        if (p.x > 70 && p.x < 120 && p.y < 165 && p.y > 73) {
          if (OnOff == true) { // 빛을 조절하는 부분
            strip_color(r, g, b);
            value_bar = 0;
            int brightness = map(value_bar, 0, 100, 0, 40);
            tft.fillRect(15 + value_bar * 2.9, 100, 290 - value_bar * 2.9, 30, WHITE);
            tft.fillRect(15, 100, value_bar * 2.9, 30, RED);
            tft.fillRect(120, 40, 120, 40 , BLACK);
            tft.setCursor(125, 50);
            tft.setTextColor(WHITE);
            tft.setTextSize(3);
            tft.print(value_bar);
            tft.println("%");
            OnOff = false;
            Serial.println("f1");
          }
          else {
            OnOff = true;
            Serial.println("t1");
          }
        }
        if (p.x > 150 && p.x < 190 && p.y > 15 && p.y < 220) {
          value_bar = (p.y + 8) / 2.24;
          int brightness = map(value_bar, 0, 100, 0, 40);
          strip_color(brightness, brightness , b);
          tft.fillRect(15 + value_bar * 2.9, 100, 290 - value_bar * 2.9, 30, WHITE);
          tft.fillRect(15, 100, value_bar * 2.9, 30, RED);
          tft.fillRect(120, 40, 120, 40 , BLACK);
          tft.setCursor(125, 50);
          tft.setTextColor(WHITE);
          tft.setTextSize(3);
          tft.print(value_bar);
          tft.println("%");
        }
      }
      else if (screen_id == 3) {//music 선택하는 화면
        if (p.x > 260 && p.y < 60) {
          homeScreen();
        }
        if (p.x > 100 && p.x < 160 && p.y < 50 && p.y > 15) {
          //play track 1
          Serial.println("track1");
          tft.fillRoundRect(20, 110, 50, 50, 5, YELLOW);
          delay(10);
          tft.fillRoundRect(20, 110, 50, 50, 5, WHITE);
          tft.setTextSize(2);
          tft.setTextColor(BLACK);
          tft.setCursor(40, 130);
          tft.print("1");
          mp3_output(15, 1);
        }
        if (p.x > 100 && p.x < 160 && p.y > 60 && p.y < 95) {
          //play track 2
          Serial.println("track2");
          tft.fillRoundRect(80, 110, 50, 50, 5, YELLOW);
          delay(10);
          tft.fillRoundRect(80, 110, 50, 50, 5, WHITE);
          tft.setTextSize(2);
          tft.setTextColor(BLACK);
          tft.setCursor(100, 130);
          tft.print("2");
          mp3_output(15, 2);
        }
        if (p.x > 100 && p.x < 160 && p.y > 105 && p.y < 140) {
          //play track 3
          Serial.println("track3");
          tft.fillRoundRect(140, 110, 50, 50, 5, YELLOW);
          delay(10);
          tft.fillRoundRect(140, 110, 50, 50, 5, WHITE);
          tft.setTextSize(2);
          tft.setTextColor(BLACK);
          tft.setCursor(160, 130);
          tft.print("3");
          mp3_output(15, 3);
        }
        if (p.x > 100 && p.x < 160 && p.y > 150 && p.y < 185) {
          //play track 4
          Serial.println("track4");
          tft.fillRoundRect(200, 110, 50, 50, 5, YELLOW);
          delay(10);
          tft.fillRoundRect(200, 110, 50, 50, 5, WHITE);
          tft.setTextSize(2);
          tft.setTextColor(BLACK);
          tft.setCursor(220, 130);
          tft.print("4");
          mp3_output(15, 4);
        }
        if (p.x > 100 && p.x < 160 && p.y > 195 && p.y < 230) {
          //play track 5
          Serial.println("track5");
          tft.fillRoundRect(260, 110, 50, 50, 5, YELLOW);
          delay(10);
          tft.fillRoundRect(260, 110, 50, 50, 5, WHITE);
          tft.setTextSize(2);
          tft.setTextColor(BLACK);
          tft.setCursor(280, 130);
          tft.print("5");
          mp3_output(15, 5);
        }
      }
      if (buttonState == false) {
        WhiteScreen();
      }
    }

  }
}
