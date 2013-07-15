/* 
 *  FCKPlugin.js
 *  ------------
 *  This is a generic file which is needed for plugins that are developed
 *  for FCKEditor. With the below statements that toolbar is created and
 *  several options are being activated.
 *
 *  See the online documentation for more information:
 *  http://wiki.fckeditor.net/
*/

// Register the related commands.
FCKCommands.RegisterCommand(
	'WBDroplets',
	new FCKDialogCommand(
		'WBDroplets',
		FCKLang["WBDropletsDlgTitle"],
		FCKPlugins.Items['WBDroplets'].Path + 'fck_wbdroplets.php',
		370,
		370
	)
);
 
// Create the "WBModules" toolbar button.
// FCKToolbarButton( commandName, label, tooltip, style, sourceView, contextSensitive )
var oWBModulesItem = new FCKToolbarButton( 'WBDroplets', FCKLang["WBDropletsBtn"], null, null, false, true ); 
oWBModulesItem.IconPath = FCKConfig.PluginsPath + 'WBDroplets/wbdroplets.gif'; 

// 'CMSContent' is the name that is used in the toolbar config.
FCKToolbarItems.RegisterItem( 'WBDroplets', oWBModulesItem );