<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ((($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android) {
  $setTime = $_POST['setTime'];
  try {
    $stmt = $con->prepare('UPDATE ino_data SET alarmTime=(:setTime)');
    $stmt->bindParam(':setTime', $setTime);

    if ($stmt->execute()) {
      $successMSG = "알람 시간을 설정했습니다.";
    } else {
      $errMSG = "사용자 추가 에러";
    }
  } catch (PDOException $e) {
    die("Database error: " . $e->getMessage());
  }
}
?>