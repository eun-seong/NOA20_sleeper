/*
 *  ESP8266 JSON Decode of server response
 *  -Manoj R. Thkuar
 *  https://circuits4you.com
 */

#include <ESP8266WiFi.h>
#include <WiFiClient.h> 
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

#include <NTPClient.h>
//#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <SoftwareSerial.h>
SoftwareSerial timeSerial(D5,D6);//(RX,TX)


WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 32400, 3600000);

const char* wifiName = "DaeWoo";
const char* wifiPass = "74187418";

//Web Server address to read/write from 
const char *host = "http://192.168.0.16:9090/NOA/inoGetData.php";

void setup() {
  
  Serial.begin(9600);
  timeSerial.begin(9600);
  timeClient.begin(9600);
  
  delay(10);
  Serial.println();
  
  Serial.print("Connecting to ");
  Serial.println(wifiName);

  WiFi.begin(wifiName, wifiPass);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());   //You can get IP address assigned to ESP
}

void loop() {


  
  HTTPClient http;    //Declare object of class HTTPClient

  Serial.print("Request Link:");
  Serial.println(host);
  
  http.begin(host);     //Specify request destination
  
  int httpCode = http.GET();            //Send the request
  String payload = http.getString();    //Get the response payload from server

  Serial.print("Response Code:"); //200 is OK
  Serial.println(httpCode);   //Print HTTP return code

  Serial.print("Returned data from Server:");
  Serial.println(payload);    //Print request response payload
  
  if(httpCode == 200)
  {
    // Allocate JsonBuffer
    // Use arduinojson.org/assistant to compute the capacity.
    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonBuffer jsonBuffer(capacity);
  
   // Parse JSON object
   // deserializeJson(jsonBuffer , payload);
    JsonObject& root = jsonBuffer.parseObject(payload);
    if (!root.success()) {
      Serial.println(F("Parsing failed!"));
      return;
    }
  
    // Decode JSON/Extract values
    Serial.println(F("Response:"));
    timeSerial.println(root["usr_statistics"][0]["alarmTime"].as<char*>());
    timeSerial.println(root["usr_statistics"][0]["totalTime"].as<char*>());
    timeSerial.println(root["usr_statistics"][0]["shallowSleep"].as<char*>());
    timeSerial.println(root["usr_statistics"][0]["deepSleep"].as<char*>());
    Serial.println(root["usr_statistics"][0]["alarmTime"].as<char*>());
    Serial.println(root["usr_statistics"][0]["totalTime"].as<char*>());
    Serial.println(root["usr_statistics"][0]["shallowSleep"].as<char*>());
    Serial.println(root["usr_statistics"][0]["deepSleep"].as<char*>());
  }
  else
  {
    Serial.println("Error in response");
  }

  http.end();  //Close connection
  
//  delay(5000);  //GET Data at every 5 seconds


  timeClient.update();
  String tstr= "";
  int h =timeClient.getHours();
  int m =timeClient.getMinutes();
  tstr = (((h+String(":")+m)));
  timeSerial.println(tstr);

  delay(3000);
}
