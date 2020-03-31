<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbcon.php');
  $stmt = $con->prepare('SELECT totalTime, shallowTime, deepTime from usr_statistics ORDER BY date DESC LIMIT 1');
  $stmt->execute();

  if ($stmt->rowCount() > 0) {
    $data = array();

    while ($row = ($stmt->fetch(PDO::FETCH_ASSOC))) {
      extract($row);

      array_push(
        $data,
        array(
          'totalTime' => $totalTime,
          'quality' => $quality,
          'awakeTime' => $awakeTime,
          'shallowSleep' => $shallowSleep,
          'deepSleep' => $deepSleep,
          'bedTime' => $bedTime,
          'getupTime' => $getupTime
        )
      );
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("usr_statistics" => $data), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);

    echo $json;
  }

?>