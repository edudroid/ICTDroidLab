<?php
/**
 *
 * @category        module
 * @package         Form
 * @author          WebsiteBaker Project
 * @copyright       2009-2011, Website Baker Org. e.V.
 * @link			http://www.websitebaker2.org/
 * @license         http://www.gnu.org/licenses/gpl.html
 * @platform        WebsiteBaker 2.8.x
 * @requirements    PHP 5.2.2 and higher
 * @version         $Id: save_field_new.php 1553 2011-12-31 15:03:03Z Luisehahne $
 * @filesource		$HeadURL: svn://isteam.dynxs.de/wb_svn/wb280/tags/2.8.3/wb/modules/form/save_field_new.php $
 * @lastmodified    $Date: 2011-12-31 16:03:03 +0100 (Sa, 31. Dez 2011) $
 * @description
 * http://devzone.zend.com/703/php-built-in-input-filtering/
 */

require('../../config.php');

// suppress to print the header, so no new FTAN will be set
$admin_header = false;
// Tells script to update when this page was last updated
$update_when_modified = true;
// Include WB admin wrapper script
require(WB_PATH.'/modules/admin.php');
/* */

$sec_anchor = (defined( 'SEC_ANCHOR' ) && ( SEC_ANCHOR != '' )  ? '#'.SEC_ANCHOR.$section['section_id'] : '' );

// check FTAN
if (!$admin->checkFTAN())
{
	$admin->print_header();
	$admin->print_error('::'.$MESSAGE['GENERIC_SECURITY_ACCESS'], ADMIN_URL.'/pages/modify.php?page_id='.$page_id.$sec_anchor);
}

// Get id
$field_id = intval($admin->checkIDKEY('field_id', false ));
if (!$field_id) {
	$admin->print_header();
	$admin->print_error($MESSAGE['GENERIC_SECURITY_ACCESS'].'::', ADMIN_URL.'/pages/modify.php?page_id='.$page_id.$sec_anchor);
}
// After check print the header to get a new FTAN
$admin->print_header();

// Validate all fields
if($admin->get_post('title') == '' OR $admin->get_post('type') == '') {
	$admin->print_error($MESSAGE['GENERIC']['FILL_IN_ALL'], WB_URL.'/modules/form/modify_field.php?page_id='.$page_id.'&section_id='.$section_id.'&field_id='.$admin->getIDKEY($field_id));
} else {
	$title = str_replace(array("[[", "]]"), '', htmlspecialchars($admin->get_post_escaped('title'), ENT_QUOTES));
	$type = $admin->add_slashes($admin->get_post('type'));
	$required = (int) $admin->add_slashes($admin->get_post('required'));
}

// If field type has multiple options, get all values and implode them
	 $value = $extra = '';
	$list_count = $admin->get_post('list_count');
	if(is_numeric($list_count)) {
		$values = array();
		for($i = 1; $i <= $list_count; $i++) {
			if($admin->get_post('value'.$i) != '') {
				$values[] = str_replace(",","&#44;",$admin->get_post('value'.$i));
			}
		}
		$value = implode(',', $values);
	}
// prepare sql-update
	switch($admin->get_post('type')):
		case 'textfield':
			$value = str_replace(array("[[", "]]"), '', $admin->get_post_escaped('value'));
			$extra = $admin->get_post_escaped('length');
			break;
		case 'textarea':
			$value = str_replace(array("[[", "]]"), '', $admin->get_post_escaped('value'));
			$extra = '';
			break;
		case 'heading':
			$extra = str_replace(array("[[", "]]"), '', $admin->get_post('template'));
			if(trim($extra) == '') $extra = '<tr><td class="frm-field_heading" colspan="2">{TITLE}{FIELD}</td></tr>';
			$extra = $admin->add_slashes($extra);
			break;
		case 'select':
			$extra = $admin->get_post_escaped('size').','.$admin->get_post_escaped('multiselect');
			break;
		case 'checkbox':
			$extra = str_replace(array("[[", "]]"), '', $admin->get_post_escaped('seperator'));
			break;
		case 'radio':
			$extra = str_replace(array("[[", "]]"), '', $admin->get_post_escaped('seperator'));
			break;
		default:
			$value = '';
			$extra = '';
			break;
	endswitch;
// Update row
	$sql  = 'UPDATE `'.TABLE_PREFIX.'mod_form_fields` ';
	$sql .= 'SET `title`=\''.$title.'\', ';
	$sql .=     '`type`=\''.$type.'\', ';
	$sql .=     '`required`=\''.$required.'\', ';
	$sql .=     '`extra`=\''.$extra.'\', ';
	$sql .=     '`value`=\''.$value.'\' ';
	$sql .= 'WHERE field_id = '.(int)$field_id.' ';
	if( $database->query($sql) ) {
		$admin->print_success($TEXT['SUCCESS'], WB_URL.'/modules/form/modify_field.php?page_id='.$page_id.'&section_id='.$section_id.'&field_id='.$admin->getIDKEY($field_id));
	}else {
		$admin->print_error($database->get_error(), WB_URL.'/modules/form/modify_field.php?page_id='.$page_id.'&section_id='.$section_id.'&field_id='.$admin->getIDKEY($field_id));
	}
// Print admin footer
	$admin->print_footer();