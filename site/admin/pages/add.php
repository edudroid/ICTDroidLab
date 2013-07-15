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
 * @requirements    PHP 5.2.2 and higher
 * @version         $Id: add.php 1494 2011-08-11 14:59:01Z Luisehahne $
 * @filesource		$HeadURL: svn://isteam.dynxs.de/wb_svn/wb280/tags/2.8.3/wb/admin/pages/add.php $
 * @lastmodified    $Date: 2011-08-11 16:59:01 +0200 (Do, 11. Aug 2011) $
 *
 */

// Create new admin object and print admin header
require('../../config.php');
require_once(WB_PATH.'/framework/class.admin.php');
// suppress to print the header, so no new FTAN will be set
$admin = new admin('Pages', 'pages_add', false);
if (!$admin->checkFTAN())
{
	$admin->print_header();
	$admin->print_error($MESSAGE['GENERIC_SECURITY_ACCESS']);
}

// Include the WB functions file
require_once(WB_PATH.'/framework/functions.php');

// Get values
$title = $admin->get_post_escaped('title');
$title = htmlspecialchars($title);
$module = preg_replace('/[^a-z0-9_-]/i', "", $admin->get_post('type')); // fix secunia 2010-93-4
$parent = intval($admin->get_post('parent')); // fix secunia 2010-91-2
$visibility = $admin->get_post('visibility');
if (!in_array($visibility, array('public', 'private', 'registered', 'hidden', 'none'))) {$visibility = 'public';} // fix secunia 2010-91-2
$admin_groups = $admin->get_post('admin_groups');
$viewing_groups = $admin->get_post('viewing_groups');

// Work-out if we should check for existing page_code
$field_set = $database->field_exists(TABLE_PREFIX.'pages', 'page_code');

// add Admin to admin and viewing-groups
$admin_groups[] = 1;
$viewing_groups[] = 1;

// After check print the header
$admin->print_header();
// check parent page permissions:
if ($parent!=0) {
	if (!$admin->get_page_permission($parent,'admin'))
    {
        $admin->print_error($MESSAGE['PAGES']['INSUFFICIENT_PERMISSIONS']);
    }

} elseif (!$admin->get_permission('pages_add_l0','system'))
{
	$admin->print_error($MESSAGE['PAGES']['INSUFFICIENT_PERMISSIONS']);
}	

// check module permissions:
if (!$admin->get_permission($module, 'module'))
{
	$admin->print_error($MESSAGE['PAGES']['INSUFFICIENT_PERMISSIONS']);
}	

// Validate data
if($title == '' || substr($title,0,1)=='.')
{
	$admin->print_error($MESSAGE['PAGES']['BLANK_PAGE_TITLE']);
}

// Check to see if page created has needed permissions
if(!in_array(1, $admin->get_groups_id()))
{
	$admin_perm_ok = false;
	foreach ($admin_groups as $adm_group)
    {
		if (in_array($adm_group, $admin->get_groups_id()))
        {
			$admin_perm_ok = true;
		} 
	}
	if ($admin_perm_ok == false)
    {
		$admin->print_error($MESSAGE['PAGES']['INSUFFICIENT_PERMISSIONS']);
	}
	$admin_perm_ok = false;
	foreach ($viewing_groups as $view_group)
    {
		if (in_array($view_group, $admin->get_groups_id()))
        {
			$admin_perm_ok = true;
		}
	}
	if ($admin_perm_ok == false)
    {
		$admin->print_error($MESSAGE['PAGES']['INSUFFICIENT_PERMISSIONS']);
	}
}

$admin_groups = implode(',', $admin_groups);
$viewing_groups = implode(',', $viewing_groups);

// Work-out what the link and page filename should be
if($parent == '0')
{
	$link = '/'.page_filename($title);
	// rename menu titles: index && intro to prevent clashes with intro page feature and WB core file /pages/index.php
	if($link == '/index' || $link == '/intro')
    {
		$link .= '_0';
		$filename = WB_PATH .PAGES_DIRECTORY .'/' .page_filename($title) .'_0' .PAGE_EXTENSION;
	} else {
		$filename = WB_PATH.PAGES_DIRECTORY.'/'.page_filename($title).PAGE_EXTENSION;
	}
} else {
	$parent_section = '';
	$parent_titles = array_reverse(get_parent_titles($parent));
	foreach($parent_titles AS $parent_title)
    {
		$parent_section .= page_filename($parent_title).'/';
	}
	if($parent_section == '/') { $parent_section = ''; }
	$link = '/'.$parent_section.page_filename($title);
	$filename = WB_PATH.PAGES_DIRECTORY.'/'.$parent_section.page_filename($title).PAGE_EXTENSION;
	make_dir(WB_PATH.PAGES_DIRECTORY.'/'.$parent_section);
}

// Check if a page with same page filename exists
$get_same_page = $database->query("SELECT page_id FROM ".TABLE_PREFIX."pages WHERE link = '$link'");
if($get_same_page->numRows() > 0 OR file_exists(WB_PATH.PAGES_DIRECTORY.$link.PAGE_EXTENSION) OR file_exists(WB_PATH.PAGES_DIRECTORY.$link.'/'))
{
	$admin->print_error($MESSAGE['PAGES']['PAGE_EXISTS']);
}

// Include the ordering class
require(WB_PATH.'/framework/class.order.php');
$order = new order(TABLE_PREFIX.'pages', 'position', 'page_id', 'parent');
// First clean order
$order->clean($parent);
// Get new order
$position = $order->get_new($parent);

// Work-out if the page parent (if selected) has a seperate template or language to the default
$query_parent = $database->query("SELECT template, language FROM ".TABLE_PREFIX."pages WHERE page_id = '$parent'");
if($query_parent->numRows() > 0)
{
	$fetch_parent = $query_parent->fetchRow();
	$template = $fetch_parent['template'];
	$language = $fetch_parent['language'];
} else {
	$template = '';
	$language = DEFAULT_LANGUAGE;
}

// Insert page into pages table
$sql  = 'INSERT INTO `'.TABLE_PREFIX.'pages` SET ';
$sql .= '`parent` = '.$parent.', ';
$sql .= '`target` = "_top", ';
$sql .= '`page_title` = "'.$title.'", ';
$sql .= '`menu_title` = "'.$title.'", ';
$sql .= '`template` = "'.$template.'", ';
$sql .= '`visibility` = "'.$visibility.'", ';
$sql .= '`position` = '.$position.', ';
$sql .= '`menu` = 1, ';
$sql .= '`language` = "'.$language.'", ';
$sql .= '`searching` = 1, ';
$sql .= '`modified_when` = '.time().', ';
$sql .= '`modified_by` = '.$admin->get_user_id().', ';
$sql .= '`admin_groups` = "'.$admin_groups.'", ';
$sql .= '`viewing_groups` = "'.$viewing_groups.'"';

$database->query($sql);
/*
$query = "INSERT INTO ".TABLE_PREFIX."pages
(page_title,menu_title,parent,template,target,position,visibility,searching,menu,language,admin_groups,viewing_groups,modified_when,modified_by) VALUES
('$title','$title','$parent','$template','_top','$position','$visibility','1','1','$language','$admin_groups','$viewing_groups','".time()."','".$admin->get_user_id()."')";
$database->query($query);
*/
if($database->is_error())
{
	$admin->print_error($database->get_error());
}

// Get the page id
$page_id = $database->get_one("SELECT LAST_INSERT_ID()");

// Work out level
$level = level_count($page_id);
// Work out root parent
$root_parent = root_parent($page_id);
// Work out page trail
$page_trail = get_page_trail($page_id);

// Update page with new level and link
$sql  = 'UPDATE `'.TABLE_PREFIX.'pages` SET ';
$sql .= '`root_parent` = '.$root_parent.', ';
$sql .= '`level` = '.$level.', ';
$sql .= '`link` = "'.$link.'", ';
$sql .= '`page_trail` = "'.$page_trail.'"';
$sql .= (defined('PAGE_LANGUAGES') && PAGE_LANGUAGES)
         && $field_set
         && ($language == DEFAULT_LANGUAGE)
         && (file_exists(WB_PATH.'/modules/mod_multilingual/update_keys.php')
         )
         ? ', `page_code` = '.(int)$page_id.' ' : ' ';
$sql .= 'WHERE `page_id` = '.$page_id;
$database->query($sql);
/*
$database->query("UPDATE ".TABLE_PREFIX."pages SET link = '$link', level = '$level', root_parent = '$root_parent', page_trail = '$page_trail' WHERE page_id = '$page_id'");
*/
if($database->is_error())
{
	$admin->print_error($database->get_error());
}
// Create a new file in the /pages dir
create_access_file($filename, $page_id, $level);

// add position 1 to new page
$position = 1;

// Add new record into the sections table
$database->query("INSERT INTO ".TABLE_PREFIX."sections (page_id,position,module,block) VALUES ('$page_id','$position', '$module','1')");

// Get the section id
$section_id = $database->get_one("SELECT LAST_INSERT_ID()");

// Include the selected modules add file if it exists
if(file_exists(WB_PATH.'/modules/'.$module.'/add.php')) {
	require(WB_PATH.'/modules/'.$module.'/add.php');
}

// Check if there is a db error, otherwise say successful
if($database->is_error()) {
	$admin->print_error($database->get_error());
} else {
	$admin->print_success($MESSAGE['PAGES']['ADDED'], ADMIN_URL.'/pages/modify.php?page_id='.$page_id);
}

// Print admin footer
$admin->print_footer();
