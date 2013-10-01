<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Send GCM</title>
    </head>
    <body>
    <table>
        <form action="/GCMBroadcast" method="post" enctype="multipart/form-data">
		<tr>
			<td>Reg. ID:</td>
			<td>	
				<input type="text" name="reg_id">
			</td>
		</tr>
		<tr>
			<td>Message:</td>
			<td>	
				<input type="text" name="message">
			</td>
		</tr>
        <tr>
        	<td>
            	<input type="submit" value="Submit">
            </td>
        </tr>
    	</form>
    </table>
    </body>
</html>