<?php
	session_start();
	$userName = NULL;
	if (isset($_SESSION['userName'])) {
		$userName = $_SESSION["userName"];
	}
?>
<html>
	<head>
		<title>Measurement uploader</title>
	</head>
	<body>
		<?php if($userName == NULL): ?>
			<form name="login" method="post" action="login.php">
				<table border="0" align="center">
				<tr>
				<th colspan="2">Member Login</th>
				</tr>
				<tr>
					<td>Username</td>
					<td><input name="userName" type="text" id="userName"></td>
				</tr>
				<tr>
					<td>Password</td>
					<td><input name="password" type="password" id="password"></td>
				</tr>
				<tr>
					<td align="left"><a href="registerForm.php">Register</a></td>
					<td align="right"><input type="submit" name="login" value="Login"></td>
				</tr>
				</table>
			</form>
		<?php else: ?>
			<h3>Hello <?=$userName?>!</h3>
			<a href = "logout.php">Log out</a><br/>
			<form action="receiveFile.php" method="post" enctype="multipart/form-data">
				<table>
					<tr><th colspan="2">Upload file</th></tr>
					<tr>
						<td><label for="userFile">Filename:</label></td>
						<td><input type="file" name="userFile" id="userFile"></td>
					</tr>
					<tr>
						<td colspan="2" align="right"><input type="submit" name="submit" value="Submit"></td>
					</tr>
				</table>
			</form>
			<?php include 'fileList.php'; ?>
		<?php endif; ?>
	</body>
</html>
