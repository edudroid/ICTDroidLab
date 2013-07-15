<?php
	$userName = NULL;
	if (isset($_SESSION['USERNAME'])) {
		$userName = $_SESSION['USERNAME'];
	}
	
	if ($userName != NULL && isset($_POST['submit'])) {
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
			$uploadFolderPath = $_SERVER['DOCUMENT_ROOT'] . '/wb/pages/uploader/upload/' . $userName; 
			$storageFile = $uploadFolderPath . '/' . $_FILES['userFile']['name'];			
			if (!file_exists($_SERVER['DOCUMENT_ROOT'] . '/wb/pages/uploader/upload/' . $userName)) {
				mkdir($uploadFolderPath);
				echo 'Upload folder created';
			}
			if (file_exists($storageFile)) {
				echo 'File already exists, unlinking.<br/>';	
				unlink($storageFile);
			}
			move_uploaded_file($_FILES['userFile']['tmp_name'], $storageFile);
			echo 'Stored in: ' . $storageFile . '<br/>';
		}
	}
	
	if ($userName != NULL && isset($_POST['download'])) {
		header('Content-type: application/octet-stream');
		header('Content-Transfer-Encoding: binary');
		header('Content-Disposition: inline; filename="'.$_POST['file'].'" ');
		readfile($_SERVER['DOCUMENT_ROOT'] . '/wb/pages/uploader/upload/'.$userName."/". $_POST['file']);
	}
	
	
?>
<html>
	<head>
		<title>Measurement uploader</title>
	</head>
	<body>
			<h3>Hello <?=$userName?>!</h3>
			<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
				<table>
					<tr><th colspan="2" align="left">Upload file</th></tr>
					<tr>
						<td><label for="userFile">Select a file to upload:</label></td>
						<td><input type="file" name="userFile" id="userFile"></td>
					</tr>
					<tr>
						<td colspan="2" align="left"><input type="submit" name="submit" value="Submit"></td>
					</tr>
				</table>
			</form>
			<table>
				<tr><hr></tr>
				<tr><th colspan="2" align="left">Uploaded files</th></tr>
				<?php
					if ($userName != NULL) {
						// Creates user's upload folder if not exists
						$uploadFolderPath = $_SERVER['DOCUMENT_ROOT'] . '/wb/pages/uploader/upload/' . $userName; 
						if (!file_exists($_SERVER['DOCUMENT_ROOT'] . '/wb/pages/uploader/upload/' . $userName)) {
							mkdir($uploadFolderPath);
							echo 'Upload folder created<br/>';
						}
						$files = scandir($uploadFolderPath);
						$fileCount = 0;
						?>							
						<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
							<table>
						<?php
						foreach ($files as &$file) {
							if (($file != ".") && ($file != "..")) {
								?>
								<tr>
									<td><?php echo $file ?><br/></td>
										<input type="hidden" name="file" value="<?php print $file; ?>"/>
										<td colspan="2" align="right"><input type="submit" name="download" value="Download"></td>
									</tr>
								<?php
								$fileCount ++;
							}
						}
						?>
							</table>
						</form>
						<?php
						unset($file);
						if ($fileCount == 0){
							echo "<tr><td>No uploaded files.</td></tr>";
						}
					}
				?>
			</table>
	</body>
</html>
