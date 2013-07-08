<?php
	$userName = $_POST['userName'];
	$password = $_POST['password'];
	include 'secretStuff/realLogin.php';
	if ($loggedIn){
		if ($_FILES['userFile']['error'] > 0) {
			echo 'BAD ' . $_FILES['userFile']['error'] . '<br>';
		} else {
			echo "OK";
			echo 'Upload: ' . $_FILES['userFile']['name'] . '<br>';
			echo 'Type: ' . $_FILES['userFile']['type'] . '<br>';
			echo 'Size: ' . ($_FILES['userFile']['size'] / 1024) . ' kB<br>';
			// Creates user's upload folder if not exists
			$uploadFolderPath = 'upload/' . $userName; 
			$storageFile = $uploadFolderPath . '/' . $_FILES['userFile']['name'];
			if (!file_exists($uploadFolderPath)) {
				mkdir($uploadFolderPath);
				echo 'Upload folder created';
			}
			if (file_exists($storageFile)) {
				unlink($storageFile);
			}
			move_uploaded_file($_FILES['userFile']['tmp_name'],$storageFile);
			echo 'Stored in: ' . $storageFile . '<br/>';
			echo '<a href="index.php">Fololdal</a>';
		}
	} else {
		echo "BAD Invalid user or password";
	}
 ?>