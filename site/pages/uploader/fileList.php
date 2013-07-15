<?php
?>
<table>
	<tr><th>Uploaded files</th></tr>
	<?php
		$userName = NULL;
		if (isset($_SESSION['userName'])) {
			$userName = $_SESSION["userName"];
		}
		if ($userName != NULL) {
			// Creates user's upload folder if not exists
			$uploadFolderPath = 'upload/' . $userName; 
			if (!file_exists('upload/' . $userName)) {
				mkdir($uploadFolderPath);
				echo 'Upload folder created<br/>';
			}
			$files = scandir($uploadFolderPath);
			$fileCount = 0;
			foreach ($files as &$file) {
				if (($file != ".") && ($file != "..")) {
					echo ('<tr><td><a href="loadFile.php?file=' .$file . '">'.$file . '</a><br/></td></tr>');
					$fileCount ++;
				}
			}
			unset($file);
			if ($fileCount == 0){
				echo "<tr><td>No uploaded files.</td></tr>";
			}
		}
	?>
</table>