<?php
	session_start();
	$userName = NULL;
	if (isset($_SESSION['userName'])) {
		$userName = $_SESSION["userName"];
	}
	if ($userName!=NULL) {
		header('Content-type: application/octet-stream');
		header('Content-Transfer-Encoding: binary');
		header('Content-Disposition: inline; filename="'.$_REQUEST['file'].'" ');
		readfile('upload/'.$userName."/". $_REQUEST['file']);
	}
	else {
		echo 'denied';
	}
?>