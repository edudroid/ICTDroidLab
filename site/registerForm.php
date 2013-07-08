<?php
?>
<html>
	<head>
		<title>Measurement uploader</title>
	</head>
	<body>
		<form name="register" method="post" action="registerUser.php">
			<table border="0" align="center">
			<tr>
			<th colspan="2">Register User</td>
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
				<td>Email (for password recovery)</td>
				<td><input name="email" type="text" id="email"></td>
			</tr>
			<tr><td colspan="2" align="right"><input type="submit" name="register" value="Register"></td></tr>
			</table>
		</form>
	</body>
</html>
