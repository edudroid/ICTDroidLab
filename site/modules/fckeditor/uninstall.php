<?php
/**
 *
 * @category        modules
 * @package         wysiwyg
 * @author          WebsiteBaker Project
 * @copyright       2004-2009, Ryan Djurovich
 * @copyright       2009-2011, Website Baker Org. e.V.
 * @link			http://www.websitebaker2.org/
 * @license         http://www.gnu.org/licenses/gpl.html
 * @platform        WebsiteBaker 2.8.x
 * @requirements    PHP 5.2.2 and higher
 * @version         $Id: uninstall.php 1374 2011-01-10 12:21:47Z Luisehahne $
 * @filesource		$HeadURL: http://svn29.websitebaker2.org/trunk/wb/modules/fckeditor/uninstall.php $
 * @lastmodified    $Date: 2010-11-23 00:55:43 +0100 (Di, 23. Nov 2010) $
 *
 */

// Must include code to stop this file being access directly
if(defined('WB_PATH') == false) { exit("Cannot access this file directly"); }

// Delete the editor directory
rm_full_dir(WB_PATH.'/modules/fckeditor/fckeditor');
