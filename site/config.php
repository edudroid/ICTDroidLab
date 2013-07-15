<?php

define('DB_TYPE', 'mysql');
define('DB_HOST', 'localhost');
define('DB_NAME', 'wbaker');
define('DB_USERNAME', 'root');
define('DB_PASSWORD', '');
define('TABLE_PREFIX', 'wb_');

define('WB_URL', 'http://localhost/wb');
define('ADMIN_DIRECTORY', 'admin'); // no leading/trailing slash or backslash!! A simple directory only!!

require_once(dirname(__FILE__).'/framework/initialize.php');

