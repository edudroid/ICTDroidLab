<?php
	# Check user name agains database
	$loggedIn = FALSE;
	include 'databaseAccess.php';
	if ($databaseHandler) {
		$stmt = $databaseHandler->prepare('select userName from user where userName = :userName and password = :passwd');
		$stmt->bindValue(':userName', $userName);
		$stmt->bindValue(':passwd', $password);
		if ($stmt->execute()) {
			while ($row = $stmt->fetch()) {
				$loggedIn = TRUE;
				break;
			}
		}
	} else {
		echo "Database not accessible";
	}
?>