<html>
	<head>
		<title>Measurement uploader</title>
	</head>
	<body>
		<form action="uploadFile.php" method="post" enctype="multipart/form-data">
			<table>
				<tr><th colspan="2">Upload file</th></tr>
				<tr>
					<td><label for="userName">Username:</label></td>
					<td><input type="text" name="userName" id="userName"></td>
				</tr>
				<tr>
					<td><label for="password">Password:</label></td>
					<td><input type="password" name="password" id="password"></td>
				</tr>
				<tr>
					<td><label for="userFile">Filename:</label></td>
					<td><input type="file" name="userFile" id="userFile"></td>
				</tr>
				<tr>
					<td colspan="2" align="right"><input type="submit" name="submit" value="Submit"></td>
				</tr>
			</table>
		</form>
	</body>
</html>
