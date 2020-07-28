<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ((($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android) {
  $usrStatus = $_POST['usrStatus'];
  try {
    $stmt = $con->prepare('UPDATE ino_data SET usrStatus=(:usrStatus)');
    $stmt->bindParam(':usrStatus', $usrStatus);

    if ($stmt->execute()) {
      $successMSG = "유저 상태 업데이트";
    } else {
      $errMSG = "유저 상태 업데이트";
    }
  } catch (PDOException $e) {
    die("Database error: " . $e->getMessage());
  }
}
?>