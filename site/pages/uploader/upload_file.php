<?php
	if ($_FILES['file']['error'] > 0) {
		echo 'BAD ' . $_FILES['file']['error'] . '<br>';
	} else {
		echo "OK";
		echo 'Upload: ' . $_FILES['file']['name'] . '<br>';
		echo 'Type: ' . $_FILES['file']['type'] . '<br>';
		echo 'Size: ' . ($_FILES['file']['size'] / 1024) . ' kB<br>';
		// Creates user's upload folder if not exists
		$uploadFolderPath = 'upload/continuousService';
		if (!file_exists($uploadFolderPath)) {
			mkdir($uploadFolderPath);
			echo 'Upload folder created';
		}
		$storageFile = $uploadFolderPath . '/' . $_FILES['file']['name'];
		if (file_exists($storageFile)) {
			unlink($storageFile);
		}
		move_uploaded_file($_FILES['file']['tmp_name'],$storageFile);
		echo 'Stored in: ' . $storageFile . '<br/>';
		echo '<a href="index.php">Fololdal</a>';
	}
 ?>