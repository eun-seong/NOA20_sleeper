<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbcon.php');

$apiKey = "5d48c9b4320dc403008c5d2f2f1d2e63";
$cityId = "1835848";
$ApiUrl = "http://api.openweathermap.org/data/2.5/weather?id=" . $cityId . "&appid=" . $apiKey;

$request = file_get_contents($ApiUrl); //example ID

$jsonPHP  = json_decode($request,true);

$weather_desc = $jsonPHP["weather"][0]["description"];
$temp = $jsonPHP["main"]["temp"];

date_default_timezone_set('Asia/Seoul');
$localdate = date("Y-m-d");
$hour = date("H");
$minute = date('i');

$stmt = $con->prepare('SELECT * from ino_data');
$stmt->execute();

if ($stmt->rowCount() > 0) {
    $data = array();

    while ($row = ($stmt->fetch(PDO::FETCH_ASSOC))) {
        extract($row);

        array_push(
            $data,
            array(
                'localdate' => $localdate,
                'hour' => $hour,
                'minute' => $minute,
                'alarmTime' => $alarmTime,
                'totalTime' => $totalTime,
                'shallowSleep' => $shallowSleep,
                'deepSleep' => $deepSleep,
                'hopeTime' => $hopeTime,
                'usrStatus' => $usrStatus,
                'bright' => $bright,
                'weather_desc' => $weather_desc,
                'temp' => $temp
            )
        );
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("usr_statistics"=>$data), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);

    echo $json;
}
?>