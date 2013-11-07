package com.modularweb.utils {
	
	import flash.text.StyleSheet;
	import flash.text.TextFormat;
	import flash.text.TextField;
	
	public class CSSManager {
		
		public static function createCSS (css:StyleSheet, cssClass:String):StyleSheet {
			var styles:Array = css.styleNames;
			var marginCSS:StyleSheet = new StyleSheet();
			
			var newCSS:StyleSheet = new StyleSheet();
			newCSS.parseCSS(css.toString());
			
			for (var i:int = 0; i < styles.length; i++) {
				var format:TextFormat = new TextFormat();
				format = css.transform(css.getStyle(styles[i]));
				
				if (format.display == "block" && styles[i].indexOf(".") == -1) {
					var tf:TextField = new TextField();
					tf.defaultTextFormat = format;
					tf.text = "define";
					
					format.leading = int(css.getStyle(styles[i]).lineHeight.replace("px", "")) - tf.getLineMetrics(0).ascent - tf.getLineMetrics(0).descent;
					var s:Object = css.getStyle(styles[i]);
					s.leading = format.leading;
					s.font = s.fontFamily;
					if (cssClass.toLowerCase() != "regular" && cssClass != "") {
						s.color = css.getStyle(styles[i] + "." + cssClass).color;
					}
					newCSS.setStyle(styles[i], s);
					//newCSS.setStyle(styles[i] + "-simple", s);
					
					s.fontSize = 1;
					s.leading = int(css.getStyle(styles[i]).marginBottom.replace("px", "")) - 1;
					
					newCSS.setStyle(styles[i] + "-MARGIN", s);
				}
				
				else {
					newCSS.setStyle(styles[i], css.getStyle(styles[i]));
				}
			}
			
			return (newCSS);
		}
		
		
		public static function includeMargins (str:String, css:StyleSheet):String {
			var styles:Array = css.styleNames;
			
			for (var i:int = 0; i < styles.length; i++) {
				if (css.getStyle(styles[i]).display == "block") {
					str = str.split('</' + styles[i] + '>').join('</' + styles[i] + '><' + styles[i] + '-MARGIN> </' + styles[i] + '-MARGIN>');
					str = str.split('<' + styles[i] + '-simple>').join('<' + styles[i] + '>').split('</' + styles[i] + '-simple>').join('</' + styles[i] + '>');
				}
			}
			
			return (str);
		}
	
	}
	
}