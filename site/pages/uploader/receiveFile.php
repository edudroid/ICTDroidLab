<?php
	$userName = NULL;
	if (isset($_SESSION['USERNAME'])) {
		$userName = $_SESSION["USERNAME"];
	}
	if ($userName != NULL) {
		# $deviceName = $_POST['deviceName'];
		$deviceName = "ICTDroidLab";
		# TODO Get device's key from
		if ($_FILES['userFile']['error'] > 0) {
			echo '{ error: ' . $_FILES['userFile']['error'] . '<br>';
		} else {
			echo 'Upload: ' . $_FILES['userFile']['name'] . '<br>';
			echo 'Type: ' . $_FILES['userFile']['type'] . '<br>';
			echo 'Size: ' . ($_FILES['userFile']['size'] / 1024) . ' kB<br>';
			// Creates user's upload folder if not exists
			$uploadFolderPath = 'upload/' . $userName; 
			$storageFile = $uploadFolderPath . '/' . $_FILES['userFile']['name'];			
			if (!file_exists('upload/' . $userName)) {
				mkdir($uploadFolderPath);
				echo 'Upload folder created';
			}
			if (file_exists($storageFile)) {
				echo 'File already exists, unlinking.<br/>';	
				unlink($storageFile);
			}
			move_uploaded_file($_FILES['userFile']['tmp_name'], $storageFile);
			echo 'Stored in: ' . $storageFile . '<br/>';
			echo '<a href="index.php">Fololdal</a>';
		}
	} else {
		echo "Please log in!";
	}
 ?>