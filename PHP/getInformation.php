<?php
$apiKey = "5d48c9b4320dc403008c5d2f2f1d2e63";
$cityId = "1835848";
$ApiUrl = "http://api.openweathermap.org/data/2.5/weather?id=" . $cityId . "&appid=" . $apiKey;


$request = file_get_contents($ApiUrl); //example ID

$jsonPHP  = json_decode($request,true);

$weather_desc = $jsonPHP["weather"][0]["description"];
$temp = $jsonPHP["main"]["temp"];

try {
    $stmt = $con->prepare('UPDATE ino_data SET weather_desc=(:weather_desc), temp=(:temp)');
    $stmt->bindParam(':weather_desc', $weather_desc);
    $stmt->bindParam(':temp', $temp);

    if ($stmt->execute()) {
      $successMSG = "날씨 정보 업데이트 성공";
    } else {
      $errMSG = "날씨 정보 업데이트 실패";
    }
  } catch (PDOException $e) {
    die("Database error: " . $e->getMessage());
  }

echo "\nfinish";
?>