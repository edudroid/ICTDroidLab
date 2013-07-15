<?php
	session_start();
	$userName=$_POST['userName']; 
	$password=$_POST['password'];
	# Check user name agains database
	include 'secretStuff/realLogin.php';
	if ($loggedIn) {
		echo "OK";
	} else {
		echo "BAD";
	}
?>