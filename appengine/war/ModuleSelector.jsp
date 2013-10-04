<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Send GCM</title>
    </head>
    <body>
    <table>
        <form action="/GCMBroadcast" method="post">
		<tr>
			<td>Reg. ID:</td>
			<td>	
				<input type="text" name="reg_id" value="APA91bEmcxMvfwaDRW6XnziD4szvxpqes7Q6iZLhKMfK0xhEsomgfkqrGFBgVdJN_rc1eo9bJD0kyZo6SkKxXpZW28_1LfC4nFxNzqIygQ0RZwtjlaORSl9I9GhuYQQp2wBwCcZpimls">
			</td>
		</tr>
		<tr>
			<td>Message:</td>
			<td>	
				<input type="text" name="message" value="http://ictdroidlab.appspot.com/serveModule?blob-key=AMIfv95F0WyxJ0PmEPe0nFuPwEG3uUQHGdBswM1BP-lItPcG-5-P2yBrM82TJvmtBQ6rM-4FcakvgDMIFwkSIFje8TpWrpEVFgxC7RIEvPTwj5qt1vbAFidpfDGEO1F0dbLxP39CDGXKmf7JtTq6ZQtViSSvkgXfIA">
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