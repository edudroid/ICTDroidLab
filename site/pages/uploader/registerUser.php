<?php
	session_start();
	include 'secretStuff/databaseAccess.php';
	if ($databaseHandler) {
		
		$userName = $_POST['userName'];
		$password = $_POST['password'];
		$email = $_POST['email'];
		$stmt = $databaseHandler->prepare('select userName from user where userName = :userName');
		$stmt->bindValue(':userName', $userName);
		$hasUser = FALSE;
		if ($stmt->execute()) {
			while ($row = $stmt->fetch()) {
				$hasUser = TRUE;
				break;
			}
		}
		
		if (!$hasUser) {
			try {
				$stmt = $databaseHandler->prepare('insert into user(userName, email, password) VALUES (:userName,:email,:password)');
				$stmt->bindValue(':userName', $userName);
				$stmt->bindValue(':password', $password);
				$stmt->bindValue(':email', $email);
				$stmt->execute();
				$_SESSION['userName']=$userName;
			} catch (PDOException $e) {
				echo $e->getMessage();
			}
			header("location:index.php");
		} else {
			echo "User already exists";
		}
	} else {
		echo "Database not accessible, see error>";
		var_dump(PDO::errorInfo());
		echo mysql_error();
	}
?>