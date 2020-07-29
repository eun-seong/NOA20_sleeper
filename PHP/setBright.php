<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ((($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android) {
  $bright = $_POST['bright'];
  try {
    $stmt = $con->prepare('UPDATE ino_data SET bright=(:bright)');
    $stmt->bindParam(':bright', $bright);

    if ($stmt->execute()) {
      $successMSG = "밝기 조절 성공";
    } else {
      $errMSG = "밝기 조절 에러";
    }
  } catch (PDOException $e) {
    die("Database error: " . $e->getMessage());
  }
}
?>