<?php
	if(isset($_SESSION['USER_ID'])){
		echo '<b>USER_ID: </b>' . $_SESSION['USER_ID'] . '<br>';
		echo '<b>GROUP_ID: </b>' . $_SESSION['GROUP_ID'] . '<br>';
		//echo '<b>User ID: </b>' . $_SESSION['GROUP_NAME'] . '<br>';
		echo '<b>USERNAME: </b>' . $_SESSION['USERNAME'] . '<br>';
		echo '<b>DISPLAY_NAME: </b>' . $_SESSION['DISPLAY_NAME'] . '<br>';
		echo '<b>EMAIL: </b>' . $_SESSION['EMAIL'] . '<br>';
		echo '<b>HOME_FOLDER: </b>' . $_SESSION['HOME_FOLDER'] . '<br>';
		echo '<b>Timezone: </b>' . $_SESSION['TIMEZONE'] . '<br>';
		echo '<b>WB_PATH: </b>' . WB_PATH . '<br>';
		echo '<b>WB_URL: </b>' . WB_URL . '<br>';
		echo '<b>ADMIN_PATH: </b>' . ADMIN_PATH . '<br>';
		echo '<b>ADMIN_URL: </b>' . ADMIN_URL . '<br>';
		echo '<b>WBMAILER_SMTP_HOST: </b>' . WBMAILER_SMTP_HOST . '<br>';
		echo '<b>DB_TYPE: </b>' . DB_TYPE . '<br>';
		echo '<b>DB_HOST: </b>' . DB_HOST . '<br>';
		echo '<b>DB_USERNAME: </b>' . DB_USERNAME . '<br>';
		echo '<b>DB_PASSWORD: </b>' . DB_PASSWORD . '<br>';
		echo '<b>DB_NAME: </b>' . DB_NAME . '<br>';
		echo '<b>TABLE_PREFIX: </b>' . TABLE_PREFIX . '<br>';
		echo '<b>PAGE_TITLE: </b>' . PAGE_TITLE . '<br>';
		echo '<b>MENU_TITLE: </b>' . MENU_TITLE . '<br>';
		echo '<b>PARENT: </b>' . PARENT . '<br>';
		echo '<b>LEVEL: </b>' . LEVEL . '<br>';
		echo '<b>VISIBILITY: </b>' . VISIBILITY . '<br>';
		echo '<b>PAGE_DESCRIPTION: </b>' . PAGE_DESCRIPTION . '<br>';
		echo '<b>TEMPLATE: </b>' . TEMPLATE . '<br>';
		echo '<b>DEFAULT_TEMPLATE: </b>' . DEFAULT_TEMPLATE . '<br>';
		echo '<b>TEMPLATE_DIR: </b>' . TEMPLATE_DIR . '<br>';
		echo '<b>THEME_URL: </b>' . THEME_URL . '<br>';
		echo '<b>SEARCH: </b>' . SEARCH . '<br>';
		echo '<b>LOGIN_URL: </b>' . LOGIN_URL . '<br>';
		echo '<b>LOGOUT_URL: </b>' . LOGOUT_URL . '<br>';
		echo '<b>FORGOT_URL: </b>' . FORGOT_URL . '<br>';
		echo '<b>PREFERENCES_URL: </b>' . PREFERENCES_URL . '<br>';
		echo '<b>SIGNUP_URL: </b>' . SIGNUP_URL . '<br>';
	}
	else{
		echo 'Welcome to out ICT DroidLab page! Please login!';
	}
?>