<?php
	session_start();
	$userName=$_POST['userName']; 
	$password=$_POST['password'];
	# Check user name agains database
	include 'secretStuff/realLogin.php';
	if ($loggedIn) {
		$_SESSION['userName']=$userName;
		header("location:index.php");
	} else {
		echo "Wrong Username or Password";
	}
?>