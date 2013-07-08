<?php
	try {
		$databaseHandler = new PDO('mysql:host=localhost;dbname=measurement_uploader','root','cicamica');
		#$databaseHandler = new PDO('mysql:host=152.66.244.50;port=3307;dbname=measurement_uploader','root','Lom5laeP');
	} catch (PDOException $e) {
		echo $e->getMessage();
	}
?>