<?php
	session_start();
	$_SESSION['userName']=NULL;
	header("location:index.php");
?>