<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbcon.php');
$stmt = $con->prepare('SELECT * from ino_data');
$stmt->execute();

if ($stmt->rowCount() > 0) {
    $data = array();

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

    echo $json;
}
?>