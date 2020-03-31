<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

// if ((($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android) {
$col_name = array('quality', 'totalTime', 'bedTime', 'getupTime');

$data = array();
for ($colName = 0; $colName < 4; $colName++) {
    $stmt = $con->prepare("SELECT {$col_name[$colName]} FROM usr_statistics");
    $stmt->execute();

    $data_ = array();
    while ($row = ($stmt->fetch(PDO::FETCH_ASSOC))) {
        array_push(
            $data_,
            $row["{$col_name[$colName]}"]
        );
    }

    array_push(
        $data,
        array("{$col_name[$colName]}" => $data_)
    );
}

header('Content-Type: application/json; charset=utf8');
$json = json_encode(array("usr_statistics" => $data), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
echo $json;
// }
?>