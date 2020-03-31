<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ((($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android) {
  $col_name = array('quality', 'totalTime', 'bedTime', 'getupTime');

  $data = array();
  for ($colName = 0; $colName < 4; $colName++) {
    $stmt = $con->prepare("SELECT ROUND(AVG({$col_name[$colName]})) as mean, MAX({$col_name[$colName]}) as max, MIN({$col_name[$colName]}) as min FROM usr_statistics");
    $stmt->execute();

    while ($row = ($stmt->fetch(PDO::FETCH_ASSOC))) {
      $data_ = array(
        'mean' => $row['mean'],
        'max' => $row['max'],
        'min' => $row['min']
      );
      array_push(
        $data,
        array("{$col_name[$colName]}" => $data_)
      );
    }
  }

  header('Content-Type: application/json; charset=utf8');
  $json = json_encode(array("usr_statistics" => $data), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
  echo $json;
}
?>