<?php
/**
 *
 * @category        admin
 * @package         pages
 * @author          WebsiteBaker Project
 * @copyright       2004-2009, Ryan Djurovich
 * @copyright       2009-2011, Website Baker Org. e.V.
 * @link			http://www.websitebaker2.org/
 * @license         http://www.gnu.org/licenses/gpl.html
 * @platform        WebsiteBaker 2.8.x
 * @requirements    PHP 5.2.2 and higher and higher
 * @version         $Id: save.php 1457 2011-06-25 17:18:50Z Luisehahne $
 * @filesource		$HeadURL: svn://isteam.dynxs.de/wb_svn/wb280/tags/2.8.3/wb/admin/pages/save.php $
 * @lastmodified    $Date: 2011-06-25 19:18:50 +0200 (Sa, 25. Jun 2011) $
 *
 */
/*
*/
// Create new admin object
require('../../config.php');
require_once(WB_PATH.'/framework/class.admin.php');

// suppress to print the header, so no new FTAN will be set
$admin = new admin('Pages', 'pages_modify', false);

// Get page & section id
if(!isset($_POST['page_id']) || !is_numeric($_POST['page_id'])) {
	header("Location: index.php");
	exit(0);
} else {
	$page_id = intval($_POST['page_id']);
}

if(!isset($_POST['section_id']) || !is_numeric($_POST['section_id'])) {
	header("Location: index.php");
	exit(0);
} else {
	$section_id = intval($_POST['section_id']);
}

// $js_back = "javascript: history.go(-1);";
$js_back = ADMIN_URL.'/pages/modify.php?page_id='.$page_id;

if (!$admin->checkFTAN())
{
	$admin->print_header();
	$admin->print_error($MESSAGE['GENERIC_SECURITY_ACCESS'],$js_back );
}
// After check print the header
$admin->print_header();

/*
if( (!($page_id = $admin->checkIDKEY('page_id', 0, $_SERVER['REQUEST_METHOD']))) )
{
	$admin->print_error($MESSAGE['GENERIC_SECURITY_ACCESS']);
}

if( (!($section_id= $admin->checkIDKEY('section_id', 0, $_SERVER['REQUEST_METHOD']))) )
{
	$admin->print_error($MESSAGE['GENERIC_SECURITY_ACCESS']);
}
*/

// Get perms
$sql  = 'SELECT `admin_groups`,`admin_users` FROM `'.TABLE_PREFIX.'pages` ';
$sql .= 'WHERE `page_id` = '.$page_id;
$results = $database->query($sql);
$results_array = $results->fetchRow();
$old_admin_groups = explode(',', str_replace('_', '', $results_array['admin_groups']));
$old_admin_users = explode(',', str_replace('_', '', $results_array['admin_users']));
$in_old_group = FALSE;
foreach($admin->get_groups_id() as $cur_gid)
{
    if (in_array($cur_gid, $old_admin_groups))
    {
        $in_old_group = TRUE;
    }
}
if((!$in_old_group) && !is_numeric(array_search($admin->get_user_id(), $old_admin_users)))
{
	$admin->print_error($MESSAGE['PAGES']['INSUFFICIENT_PERMISSIONS']);
}
// Get page module
$sql  = 'SELECT `module` FROM `'.TABLE_PREFIX.'sections` ';
$sql .= 'WHERE `page_id`='.$page_id.' AND `section_id`='.$section_id;
$module = $database->get_one($sql);
if(!$module)
{
	$admin->print_error( $database->is_error() ? $database->get_error() : $MESSAGE['PAGES']['NOT_FOUND']);
}
//$results = $database->query($sql);
//if($database->is_error()) {
//	$admin->print_error($database->get_error());
//}
//if($results->numRows() == 0) {
//	$admin->print_error($MESSAGE['PAGES']['NOT_FOUND']);
//}
//$results_array = $results->fetchRow();
//$module = $results_array['module'];

// Update the pages table
$now = time();
$sql  = 'UPDATE `'.TABLE_PREFIX.'pages` SET ';
$sql .= '`modified_when` = '.$now.', `modified_by` = '.$admin->get_user_id().' ';
$sql .= 'WHERE `page_id` = '.$page_id;
$database->query($sql);

// Include the modules saving script if it exists
if(file_exists(WB_PATH.'/modules/'.$module.'/save.php'))
{
	include_once(WB_PATH.'/modules/'.$module.'/save.php');
}
// Check if there is a db error, otherwise say successful
if($database->is_error())
{
	$admin->print_error($database->get_error(), ADMIN_URL.'/pages/modify.php?page_id='.$results_array['page_id'] );
} else {
	$admin->print_success($MESSAGE['PAGES']['SAVED'], ADMIN_URL.'/pages/modify.php?page_id='.$results_array['page_id'] );
}

// Print admin footer
$admin->print_footer();

?>