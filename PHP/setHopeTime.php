<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ((($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android) {
  $hopeTime = $_POST['hopeTime'];
  try {
    $stmt = $con->prepare('UPDATE ino_data SET hopeTime=(:hopeTime)');
    $stmt->bindParam(':hopeTime', $hopeTime);

    if ($stmt->execute()) {
      $successMSG = "희망 수면 시간을 설정하였습니다";
    } else {
      $errMSG = "희망 수면 시간 설정 에러";
    }
  } catch (PDOException $e) {
    die("Database error: " . $e->getMessage());
  }
}
?>