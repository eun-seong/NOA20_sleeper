<!-- statistics 테이블에 레코드 추가 -->
<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ((($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android) {
    $totalTime      = $_POST['totalTime'];
    $bedTime        = $_POST['bedTime'];
    $getupTime      = $_POST['getupTime'];
    $quality        = $_POST['quality'];
    $awakeTime      = $_POST['awakeTime'];
    $shallowSleep   = $_POST['shallowSleep'];
    $deepSleep      = $_POST['deepSleep'];

    try {
        $stmt = $con->prepare("INSERT INTO usr_statistics (totalTime, bedTime, getupTime, quality, awakeTime, shallowSleep, deepSleep)".
         "VALUES({$totalTime}, {$bedTime}, {$getupTime}, {$quality}, {$awakeTime}, {$shallowSleep}, {$deepSleep})");
        $stmt->execute();

        $stmt = $con->prepare("SELECT * FROM usr_statistics");
        $stmt->execute();

        if ($stmt->rowCount() > 7) {
            $stmt = $con->prepare("DELETE FROM usr_statistics LIMIT 1");
            $stmt->execute();
        }
        $stmt = $con->prepare("UPDATE ino_data SET totalTime={$totalTime}, shallowSleep={$shallowSleep}, deepSleep={$deepSleep}");
        $stmt->execute();
    } catch (PDOException $e) {
        die("Database error: " . $e->getMessage());
    }
}

?>