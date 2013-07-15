<?php
/**
 *
 * @category        modules
 * @package         captcha_control
 * @author          WebsiteBaker Project
 * @copyright       2009-2011, Website Baker Org. e.V.
 * @link            http://www.websitebaker2.org/
 * @license         http://www.gnu.org/licenses/gpl.html
 * @platform        WebsiteBaker 2.8.x
 * @requirements    PHP 5.2.2 and higher
 * @version         $Id: install.php 1536 2011-12-10 04:22:29Z Luisehahne $
 * @filesource      $HeadURL: svn://isteam.dynxs.de/wb_svn/wb280/tags/2.8.3/wb/modules/captcha_control/install.php $
 * @lastmodified    $Date: 2011-12-10 05:22:29 +0100 (Sa, 10. Dez 2011) $
 *
 */

// prevent this file from being accessed directly
/* -------------------------------------------------------- */
if(defined('WB_PATH') == false)
{
	// Stop this file being access directly
		die('<head><title>Access denied</title></head><body><h2 style="color:red;margin:3em auto;text-align:center;">Cannot access this file directly</h2></body></html>');
}
/* -------------------------------------------------------- */

$table = TABLE_PREFIX.'mod_captcha_control';
$database->query("DROP TABLE IF EXISTS `$table`");

$database->query("CREATE TABLE IF NOT EXISTS `$table` (
	`enabled_captcha` VARCHAR(1) NOT NULL DEFAULT '1',
	`enabled_asp` VARCHAR(1) NOT NULL DEFAULT '0',
	`captcha_type` VARCHAR(255) NOT NULL DEFAULT 'calc_text',
	`asp_session_min_age` INT(11) NOT NULL DEFAULT '20',
	`asp_view_min_age` INT(11) NOT NULL DEFAULT '10',
	`asp_input_min_age` INT(11) NOT NULL DEFAULT '5',
	`ct_text` LONGTEXT NOT NULL
	) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci"
);

// add new row using the table default values defined above
$database->query("
	INSERT INTO `$table`
		(`enabled_captcha`, `enabled_asp`, `captcha_type`)
	VALUES
		('1', '1', 'calc_text')
");
