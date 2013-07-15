<?php
echo "HELLO";
		header('Content-type: application/octet-stream');
		header('Content-Transfer-Encoding: binary');
		header('Content-Disposition: inline; filename="'.$_REQUEST['file'].'" ');
		readfile($_SERVER['DOCUMENT_ROOT'] . '/wb/pages/uploader/upload/'.$userName."/". $_REQUEST['file']);
?>