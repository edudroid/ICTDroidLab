<?php

// $Id: class.cssparser.php 1420 2011-01-26 17:43:56Z Luisehahne $

/*

 Website Baker Project <http://www.websitebaker.org/>
 Copyright (C) 2004-2009, Ryan Djurovich

 Website Baker is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 Website Baker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Website Baker; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/

/*
 * Class to parse css information.
 *
 * See the readme file : http://www.phpclasses.org/browse/file/4685.html
 *
 * $Id: class.cssparser.php 1420 2011-01-26 17:43:56Z Luisehahne $
 *
 * @author http://www.phpclasses.org/browse/package/1289.html
 * @package PhpGedView
 * @subpackage Charts
 *
 * added function GetXML to the cssparser class (Christian Sommer, 2007)
 *
 */
// Must include code to stop this file being access directly
if(defined('WB_PATH') == false) { die("Cannot access this file directly"); }

class cssparser {
	var $css;
	var $html;
  
	function cssparser($html = true) {
		// Register "destructor"
		register_shutdown_function(array(&$this, "finalize"));
		$this->html = ($html != false);
		$this->Clear();
	}
  
	function finalize() {
		unset($this->css);
	}
  
	function Clear() {
		unset($this->css);
		$this->css = array();
		if($this->html) {
			$this->Add("ADDRESS", "");
			$this->Add("APPLET", "");
			$this->Add("AREA", "");
			$this->Add("A", "");
			$this->Add("A:visited", "");
			$this->Add("BASE", "");
			$this->Add("BASEFONT", "");
			$this->Add("BIG", "");
			$this->Add("BLOCKQUOTE", "");
			$this->Add("BODY", "");
			$this->Add("BR", "");
			$this->Add("B", "");
			$this->Add("CAPTION", "");
			$this->Add("CENTER", "");
			$this->Add("CITE", "");
			$this->Add("CODE", "");
			$this->Add("DD", "");
			$this->Add("DFN", "");
			$this->Add("DIR", "");
			$this->Add("DIV", "");
			$this->Add("DL", "");
			$this->Add("DT", "");
			$this->Add("EM", "");
			$this->Add("FONT", "");
			$this->Add("FORM", "");
			$this->Add("H1", "");
			$this->Add("H2", "");
			$this->Add("H3", "");
			$this->Add("H4", "");
			$this->Add("H5", "");
			$this->Add("H6", "");
			$this->Add("HEAD", "");
			$this->Add("HR", "");
			$this->Add("HTML", "");
			$this->Add("IMG", "");
			$this->Add("INPUT", "");
			$this->Add("ISINDEX", "");
			$this->Add("I", "");
			$this->Add("KBD", "");
			$this->Add("LINK", "");
			$this->Add("LI", "");
			$this->Add("MAP", "");
			$this->Add("MENU", "");
			$this->Add("META", "");
			$this->Add("OL", "");
			$this->Add("OPTION", "");
			$this->Add("PARAM", "");
			$this->Add("PRE", "");
			$this->Add("P", "");
			$this->Add("SAMP", "");
			$this->Add("SCRIPT", "");
			$this->Add("SELECT", "");
			$this->Add("SMALL", "");
			$this->Add("STRIKE", "");
			$this->Add("STRONG", "");
			$this->Add("STYLE", "");
			$this->Add("SUB", "");
			$this->Add("SUP", "");
			$this->Add("TABLE", "");
			$this->Add("TD", "");
			$this->Add("TEXTAREA", "");
			$this->Add("TH", "");
			$this->Add("TITLE", "");
			$this->Add("TR", "");
			$this->Add("TT", "");
			$this->Add("UL", "");
			$this->Add("U", "");
			$this->Add("VAR", "");
		}
	}
  
	function SetHTML($html) {
		$this->html = ($html != false);
	}
  
	function Add($key, $codestr) {
		$key = strtolower($key);
		//    $codestr = strtolower($codestr);
		if(!isset($this->css[$key])) {
			$this->css[$key] = array();
		}
		$codes = explode(";",$codestr);
		if(count($codes) > 0) {
			foreach($codes as $code) {
				$code = trim($code);
				@list($codekey, $codevalue) = explode(":",$code);
				if(strlen($codekey) > 0) {
					$this->css[$key][trim($codekey)] = trim($codevalue);
				}
			}
		}
	}
  
	function Get($key, $property) {
		$key = strtolower($key);
		//    $property = strtolower($property);
		@list($tag, $subtag) = explode(":",$key);
		@list($tag, $class) = explode(".",$tag);
		@list($tag, $id) = explode("#",$tag);
		$result = "";
		foreach($this->css as $_tag => $value) {
			@list($_tag, $_subtag) = explode(":",$_tag);
			@list($_tag, $_class) = explode(".",$_tag);
			@list($_tag, $_id) = explode("#",$_tag);
            $tagmatch = (strcmp($tag, $_tag) == 0) | (strlen($_tag) == 0);
			$subtagmatch = (strcmp($subtag, $_subtag) == 0) | (strlen($_subtag) == 0);
			$classmatch = (strcmp($class, $_class) == 0) | (strlen($_class) == 0);
			$idmatch = (strcmp($id, $_id) == 0);
            if($tagmatch & $subtagmatch & $classmatch & $idmatch) {
				$temp = $_tag;
				if((strlen($temp) > 0) & (strlen($_class) > 0)) {
					$temp .= ".".$_class;
				}
				elseif(strlen($temp) == 0) {
					$temp = ".".$_class;
				}
				if((strlen($temp) > 0) & (strlen($_subtag) > 0)) {
					$temp .= ":".$_subtag;
				}
				elseif(strlen($temp) == 0) {
					$temp = ":".$_subtag;
				}
				if(isset($this->css[$temp][$property])) {
					$result = $this->css[$temp][$property];
				}
			}
		}
		return $result;
	}
  
	function GetSection($key) {
		$key = strtolower($key);
        @list($tag, $subtag) = explode(":",$key);
		@list($tag, $class) = explode(".",$tag);
		@list($tag, $id) = explode("#",$tag);
		$result = array();
		foreach($this->css as $_tag => $value) {
			@list($_tag, $_subtag) = explode(":",$_tag);
			@list($_tag, $_class) = explode(".",$_tag);
			@list($_tag, $_id) = explode("#",$_tag);
			$tagmatch = (strcmp($tag, $_tag) == 0) | (strlen($_tag) == 0);
			$subtagmatch = (strcmp($subtag, $_subtag) == 0) | (strlen($_subtag) == 0);
			$classmatch = (strcmp($class, $_class) == 0) | (strlen($_class) == 0);
			$idmatch = (strcmp($id, $_id) == 0);
			if($tagmatch & $subtagmatch & $classmatch & $idmatch) {
				$temp = $_tag;
				if((strlen($temp) > 0) & (strlen($_class) > 0)) {
					$temp .= ".".$_class;
				}
				elseif(strlen($temp) == 0) {
					$temp = ".".$_class;
				}
				if((strlen($temp) > 0) & (strlen($_subtag) > 0)) {
					$temp .= ":".$_subtag;
				}
				elseif(strlen($temp) == 0) {
					$temp = ":".$_subtag;
				}	
				foreach($this->css[$temp] as $property => $value) {
					$result[$property] = $value;
				}
			}
		}
		return $result;
	}
  
	function ParseStr($str) {
		$this->Clear();
		// Remove comments
		$str = preg_replace("/\/\*(.*)?\*\//Usi", "", $str);
		// Parse this damn csscode
		$parts = explode("}",$str);
		if(count($parts) > 0) {
			foreach($parts as $part) {
				@list($keystr,$codestr) = explode("{",$part);
				$keys = explode(",",trim($keystr));
				if(count($keys) > 0) {
					foreach($keys as $key) {
						if(strlen($key) > 0) {
							$key = str_replace("\n", "", $key);
							$key = str_replace("\\", "", $key);
							$this->Add($key, trim($codestr));
						}
					}
				}
			}
		}
		//
		return (count($this->css) > 0);
	}
  
	function Parse($filename) {
		$this->Clear();
		if(file_exists($filename)) {
			return $this->ParseStr(file_get_contents($filename));
		}
		else {
			return false;
		}
	}
	
	function GetCSS() {
		$result = "";
		foreach($this->css as $key => $values) {
			$result .= $key." {\n";
			foreach($values as $key => $value) {
				$result .= "  $key: $value;\n";
			}
			$result .= "}\n\n";
		}
		return $result;
	}

	function GetXML() {
		// Construction of "fckstyles.xml" for FCKeditor
		$styles = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"."\n";
		$styles .= '<Styles>'."\n";
		foreach ($this->css as $key => $value) {
			// skip all CSS that are a combination of CSS
			// skip CSS that are binded on an EVENT
			if (strpos($key, " ") === false && strpos($key, ":") === false) {
				$pieces = explode(".", $key, 2);
				if (strcmp($pieces[0], "") != 0) {
					continue;
				} else {
					$style_elem = "span";
				}
				if (strcmp($pieces[1], "") != 0) {
					$style_class_name = $pieces[1];
				} else {
					$style_class_name = $pieces[0];
				}
				$styles .= '<Style name="'.$style_class_name.'" element="'.$style_elem.'"';
				if (strcmp($style_class_name, $style_elem) != 0) {
					$styles .= '>'."\n".'<Attribute name="class" value="'.$style_class_name.'" />'."\n".'</Style>'."\n";
				} else {
					$styles .= '/>'."\n";
				}
			}
		}
		$styles .= '</Styles>'."\n";
		return trim($styles);
	}
}
?>