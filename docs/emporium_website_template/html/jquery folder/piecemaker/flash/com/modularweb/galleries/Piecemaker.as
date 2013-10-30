/////////////////////////////////   Author: Björn Crüger
/////   THE PIECEMAKER V2   /////   Copyright: Modularweb GmbH & Co. KG
/////////////////////////////////   Date: December 20th, 2010

package com.modularweb.galleries {
	
	// Importing all classes used by the Piecemaker V2
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.ProgressEvent;
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Loader;
	import flash.display.LoaderInfo;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.text.AntiAliasType;
	import flash.text.TextFieldAutoSize;
	import flash.text.StyleSheet;
	import flash.net.URLRequest;
	import flash.net.URLLoader;
	import flash.net.navigateToURL;
	import flash.filters.BitmapFilter;
    import flash.filters.BitmapFilterQuality;
    import flash.filters.BlurFilter;
	import flash.utils.Timer;
	import flash.events.TimerEvent;
	import fl.video.FLVPlayback;
	import fl.video.VideoEvent;
	import flash.geom.Rectangle;
	import flash.geom.Point;
	import caurina.transitions.Tweener;
	import caurina.transitions.properties.ColorShortcuts;
	import com.modularweb.utils.CSSManager;
	
	
	public class Piecemaker extends MovieClip {
		
		private var _xml:XML; // Reference of the Piecemaker XML structure
		private var css:StyleSheet;  // Reference of the CSS with textstyles
		private var xmlSource:String; 
		private var cssSource:String;
		
		private var container:MovieClip;  // A MovieClip containing all the cubes during transitions
		private var image:MovieClip;  // The image, which is shown between transitions (can contain Bitmap, loaded SWF, video)
		private var movie:FLVPlayback;   // When content is a video, it will be shown in this instance
		private var imageMask:Sprite;  // A mask for the image MovieClip, to cut off larger images or SWFs that exceed the borders
		private var dropshadow:MovieClip;  // The shadow underneath the Piecemaker
		private var images:Array;  // An Array to store all the loaded images
		private var cubes:Array;  // An array to store all cubes for a transition
		
		private var controls:MovieClip;  // The controls like "PLAY", "STOP", "LINK", "INFO" are stored in this MovieClip
		private var menu:MovieClip;  // The menu to navigate through the images is store in this MovieClip
		private var tooltip:MovieClip;  // The tooltip
		private var tooltipFormat:TextFormat;  // The TextFormat for the tooltip text
		private var tooltipTimer:Timer;  // A timer to remove the tooltip slighly after a rollout
		
		private var currentImage:int;  // The identifier of the current image
		private var nextImage:int;  // The identifier of the next image during a transition
		private var prevImage:int;  // The identifier of the previous image during a transition
		private var transitionCount:int;  // Counting transitions to specify, which transition style should be applied next
		
		public var countdown:Number = 0;  // Countdown until next image is shown
		private var isPlaying:Boolean;  // Specifies whether or not autoplay is active
		private var loadingMedia:int;  // identifier of the currently loading media (image, swf, video)
		
		
		public function Piecemaker () {
			ColorShortcuts.init();
			
			// Defining sources of XML and CSS files. If not specified in the Flashvars, the files have to be located in 
			// the same directory as the Piecemaker and bei named "piecemaker.xml" and "piecemaker.css" 
			xmlSource = "piecemaker.xml";
			cssSource = "piecemaker.css";
			
			var flashvars:Object = LoaderInfo(this.root.loaderInfo).parameters;
			if (flashvars.xmlSource != undefined) {
				xmlSource = flashvars.xmlSource;
			}
			if (flashvars.cssSource != undefined) {
				cssSource = flashvars.cssSource;
			}
			
			// Loading the XML file
			var xmlLoader:URLLoader = new URLLoader();
			xmlLoader.addEventListener(Event.COMPLETE, loadCSS);
			xmlLoader.load(new URLRequest(xmlSource));
		}
		
		
		private function loadCSS (e:Event) {
			// Assigning the loaded XML to the _xml variable
			_xml = new XML(e.target.data);
			
			// Loading the CSS file
			var cssLoader:URLLoader = new URLLoader();
			cssLoader.addEventListener(Event.COMPLETE, init);
			cssLoader.load(new URLRequest(cssSource));
		}
		
		
		public function init (e:Event) {
			// Assigning the loaded CSS to the css variable
			css = new StyleSheet();
			css.parseCSS(e.target.data);
			css = CSSManager.createCSS(css, "regular");
			
			// Settings projection and fieldOfView, installing listener to update fieldOfView, when stage is resized
			stage.addEventListener (Event.RESIZE, stageResize);
			root.transform.perspectiveProjection.projectionCenter = new Point(this.x, this.y);
			root.transform.perspectiveProjection.fieldOfView = int(_xml.Settings.@FieldOfView);
			
			// Autoplay setup based on the xml property
			if (int(_xml.Settings.@Autoplay) > 0) {
				isPlaying = true;
			}
			
			// Creating an Array to store all the loaded images
			images = new Array();
			
			// Creating the dropshadow MC incl. positioning, scaling, alpha and BlurFilter setup based on xml specs
			dropshadow = new MovieClip();
			dropshadow.x = int(_xml.Settings.@ImageWidth) / -2 * Number(_xml.Settings.@DropShadowScale);
			dropshadow.y = int(_xml.Settings.@ImageHeight) / 2 + int(_xml.Settings.@DropShadowDistance) - int(_xml.Settings.@MenuDistanceY) / 2;
			dropshadow.z = int(_xml.Settings.@ImageHeight) / -2;
			dropshadow.scaleX = Number(_xml.Settings.@DropShadowScale);
			dropshadow.alpha = Number(_xml.Settings.@DropShadowAlpha);		
            dropshadow.filters = [new BlurFilter(int(_xml.Settings.@DropShadowBlurX), int(_xml.Settings.@DropShadowBlurY), BitmapFilterQuality.HIGH)];
			addChild(dropshadow);
			
			// Creating and positioning a container MC, which will store all the cubes later on
			container = new MovieClip();
			container.x = Math.round(int(_xml.Settings.@ImageWidth) / -2);
			container.y = Math.round(int(_xml.Settings.@MenuDistanceY) / -2) - 0.5;
			addChild(container);
			
			// Creating a menu container MC, which will store the menu items later on
			menu = new MovieClip();
			menu.y = Math.round( (int(_xml.Settings.@ImageHeight) + int(_xml.Settings.@MenuDistanceY)) / 2 );
			addChild(menu);
			
			// Creating and positioning the single menu items
			for (var i:int = 0; i < _xml.Contents.children().length(); i++) {
				var mi:MovieClip = new MenuItem();
				mi.x = i * int(_xml.Settings.@MenuDistanceX) + mi.width / 2;
				mi.alpha = 0;
				menu.addChild(mi);
			}
			
			// Positioning the menu container MC based on its with to have it centeres underneath the actual image part
			menu.x = Math.round(menu.width / -2);
			
			
			// Setting up the tooltip based on the xml specs
			tooltip = new Tooltip();
			tooltip.y = menu.y - 15;
			tooltip.alpha = 0;
			tooltip.visible = false;
			tooltip.ttSurface.height = int(_xml.Settings.@TooltipHeight);
			tooltip.ttSurface.y = int(_xml.Settings.@TooltipHeight) * -1;
			tooltip.ttMask.height = int(_xml.Settings.@TooltipHeight);
			tooltip.ttMask.y = int(_xml.Settings.@TooltipHeight) * -1;
			Tweener.addTween (tooltip.ttSurface, {_color:int(_xml.Settings.@TooltipColor)});
			Tweener.addTween (tooltip.ttArrow, {_color:int(_xml.Settings.@TooltipColor)});
			
			// Creating the tooltip TextFormat
            var style:Object = css.getStyle(_xml.Settings.@TooltipTextStyle);
            tooltipFormat = css.transform(style);
			tooltipFormat.color = int(_xml.Settings.@TooltipTextColor);
			tooltipFormat.align = "left";
			tooltipFormat.leftMargin = int(_xml.Settings.@TooltipMarginLeft);
			tooltipFormat.rightMargin = int(_xml.Settings.@TooltipMarginRight);
			
			// Setting up an image mask, which will make sure that e.g. no parts of loaded SWF files will be seen outside the image dimensions
			imageMask = new Sprite();
			imageMask.graphics.beginFill(0x00FF00, 0);
			imageMask.graphics.drawRect(container.x, container.y + 0.5 - Math.round(int(_xml.Settings.@ImageHeight) / 2), int(_xml.Settings.@ImageWidth), int(_xml.Settings.@ImageHeight));
			imageMask.graphics.endFill();
			addChild(imageMask);
			
			// Starting the loading of the images
			loadImage();
			
			// There is a occasional bug with the dropshadow to be fixed. See the function resetShadow for more info.
			addEventListener (Event.ENTER_FRAME, resetShadow);
		}
	
		
		private function loadImage () {
			// Creating the URLRequest. If the element is an image, the image is being requested. If the element is an SWF or video file
			// the related preview image is being requested
			var req:URLRequest;
			if (_xml.Contents.child(images.length).name() == "Image") {
				req = new URLRequest(_xml.Contents.child(images.length).@Source);
			}
			else {
				req = new URLRequest(_xml.Contents.child(images.length).Image.@Source);
			}
			
			// Creating the Loader to load the URLRequest
			var l:Loader = new Loader();
			l.contentLoaderInfo.addEventListener (Event.COMPLETE, imageLoaded);
			l.load(req);
		}
		
		
		private function imageLoaded (e:Event) {
			// Adding the loaded image to the images Array
			images.push(e.target.content as Bitmap);
			
			// If the loaded image is the first one, the openPiecemaker function is called. If the image is not the last one,
			// the loadImage function is called again to load the next image.
			if (images.length == 1) {
				openPiecemaker();
			}
			if (images.length < _xml.Contents.children().length()) {
				loadImage();
			}
			
			// The related menu item is being actived by the menuActivation function
			menuActivation (menu.getChildAt(images.length - 1) as MovieClip);
		}
		
		
		private function openPiecemaker () {
			// Starting the first transition width prevImage = -1
			if (images.length > 0) {
				prevImage = -1;
				nextImage = 0;
				currentImage = 0;
				
				buildCubes();
				firstTransition();
				
				// Initiating the setup for all menu items
				for (var i:int = 0; i < menu.numChildren; i++) {
					menuSetup (menu.getChildAt(i) as MovieClip);
				}
			}
		}
		
		
		private function changeImage () {
			// This function controls the transition between two images
			
			buildCubes();  // This function sets up the cubes within the container
			startTransition();  // This function starts the actual transition
			
			// If the previous content was a Video, the volume is faded to 0 and finally the movie is being stopped
			if (_xml.Contents.child(prevImage).name() == "Video") {
				Tweener.addTween (movie, {volume:0, time:1, transition:"linear", onComplete:function(){movie.stop()} });
			}
			
			// Finally the menu items are updated. The item for the previous content is brought back to normal state and the item
			// for the new content is set to active. Also all click listeners are temporarily eliminated, as no new transition shall
			// be started, while another transition is still going on.
			for (var i:int = 0; i < menu.numChildren; i++) {
				var mi:MovieClip = menu.getChildAt(i) as MovieClip;
				mi.button.removeEventListener (MouseEvent.CLICK, menuClick);
				
				if (i == prevImage) {
					Tweener.addTween (mi.colorMC, {width:0, height:0, time:0.5});
					Tweener.addTween (mi.circleMC, {_color:int(_xml.Settings.@MenuColor1), time:0.5, transition:"linear"});
					Tweener.addTween (mi.maskMC, {width:10, height:10, time:0.5});
				}
				if (i == nextImage) {
					mi.countdownMC.graphics.clear();
					Tweener.addTween (mi.colorMC, {width:7, height:7, time:0.5});
					Tweener.addTween (mi.circleMC, {_color:int(_xml.Settings.@MenuColor2), time:0.5, transition:"linear"});
					Tweener.addTween (mi.maskMC, {width:14, height:14, time:0.5});
				}
			}
		}
		
		
		private function buildCubes () {
			// Stopping the countdown tween
			Tweener.removeTweens (this);
			
			// Drawing a Bitmap to be sliced later on. For openPiecemaker function with prevImage = -1, the Bitmap is only a
			// dark grey surface. Otherwise a snapshot is taken from the current state of the image MovieClip
			var bmd:BitmapData;
			var pi:Bitmap;
			if (prevImage < 0) {
				bmd = new BitmapData(int(_xml.Settings.@ImageWidth), int(_xml.Settings.@ImageHeight), false, 0x333333);
				pi = new Bitmap(bmd);
			}
			else {
				image.mask = null;
				bmd = new BitmapData(int(_xml.Settings.@ImageWidth), int(_xml.Settings.@ImageHeight), false, 0x333333);
				bmd.draw(image);
				pi = new Bitmap(bmd);
			}
			
			// Number of pieces is specified. If openPiecemaker (prevImage = -1), pieces is set to 7. Otherwise the transition
			// settings specified in the xml are used, one after another
			var pieces:int = int(_xml.Transitions.Transition[transitionCount].@Pieces);
			if (prevImage < 0) {pieces = 7};
			
			var prevPieces:Array = sliceImage (pi, pieces);  // Calling a function to slice the previuos image (the Bitmap created above)
			var nextPieces:Array = sliceImage (images[nextImage], pieces);  // Calling a function to slice the next image (taken from the images array)
			
			var iw:int = int(_xml.Settings.@ImageWidth); // imageWidth
			var ih:int = int(_xml.Settings.@ImageHeight); // imageHeight
			var pw:int = prevPieces[0].width; // pieceWidth
			
			// Removing all previous elements from the container MovieClip and the dropshadow MovieClip
			while (container.numChildren > 0) {
				container.removeChildAt(0);
			}
			while (dropshadow.numChildren > 0) {
				dropshadow.removeChildAt(0);
			}
			
			// Clearing the cubes Array
			cubes = new Array();
			
			// Building the actual cubes. The cube is a MovieClip containing 4 side Sprites. The side Sprites are being rotated on the
			// x-axis. Each side then has a surface Sprite, which holds the actual Bitmap to be shown. The Bitmaps are taken from the Arrays
			// prevPieces and nextPieces, which have been created above. Every side has a single color background, to surfaces hold these 
			// Bitmaps.
			for (var i:int = 0; i < prevPieces.length; i++) {
				var cube:MovieClip = new MovieClip();
				cube.ID = i;
				cube.orderChanged = false;
				cube.x = i * pw;
				cube.z = ih / 2;
				cubes.push(cube);
				
				for (var ii:int = 0; ii < 4; ii++) {
					var side:Sprite = new Sprite(); 
					side.rotationX = -90 * ii;  
				  
					var surface:Sprite = new Sprite();    
					surface.z = ih / -2; 
					surface.graphics.beginFill(int(_xml.Settings.@LoaderColor), 1);  
					surface.graphics.drawRect(0, ih / -2, nextPieces[i].width, ih);  
					surface.graphics.endFill();
					
					if (ii == 0) {
						surface.addChild(prevPieces[i]);
						prevPieces[i].y = ih / -2;
					}
					
					if (ii == 1 && prevImage < nextImage || ii == 3 && prevImage > nextImage) {
						surface.addChild(nextPieces[i]);
						nextPieces[i].y = ih / -2;
					} 
				  
					side.addChild(surface);
					cube.addChildAt(side, 0);
				}
				
				// Setting up the inner side for the cube. There is just one inner side per cube, as only one will be shown anyway.
				// Whether it's being appended on the left or the right is determined by whether the cube is in the first 50% of all
				// cubes or not.
				var inner:Sprite = new Sprite();
				inner.graphics.beginFill(int(_xml.Settings.@InnerSideColor), 1);
				inner.graphics.drawRect(0, ih / -2, ih, ih);
				inner.graphics.endFill();
				inner.rotationY = -90;
				inner.z = ih / -2;
				cube.addChildAt(inner, 3);
				
				if (i < prevPieces.length / 2) {
					container.addChild(cube);
					inner.x = prevPieces[i].width;
				}
				else {
					container.addChildAt(cube, 0);
				}
				
				// These three lines are rather important. The native Flash 3D engine causes some sort of pixel shifting. This can also
				// be seen in the first version of the Piecemaker at the end of each transition. These little modifications on the scale
				// properties solve this issue.
				cube.scaleX = nextPieces[i].width / ( nextPieces[i].width + 1 );
				cube.scaleY = cube.height / ( cube.height + 1 );
				cube.scaleZ = cube.height / ( cube.height + 1 );
				
				// Every cube gets its own counterpart within the dropshadow MovieClip. It's a black surface with the same width as the
				// actual cube, rotated by 90° on the x-axis.
				var s:Sprite = new Sprite();
				s.x = cube.x;
				s.z = cube.z;
				s.graphics.beginFill(0x000000, 1);
				s.graphics.drawRect(0, 0, nextPieces[i].width, ih);
				s.graphics.endFill();
				s.rotationX = 90;	
				dropshadow.addChild(s);
			}
			
			// If the number of pieces is uneven, the inner side of the middle cube is being made invisible, as both inner sides would be
			// hidden behind the front of the cube at any time.
			if (prevPieces.length / 2 != Math.round(prevPieces.length / 2)) {
				cubes[Math.floor(prevPieces.length / 2)].getChildAt(3).visible = false;
			}
			
		}
		
		
		private function sliceImage (origin:Bitmap, pieces:int):Array {
			// This function slices an image and returns an Array with its parts. It's started with an origin Bitmap and number of pieces.
			// Therefor a mc MovieClip is set up, in which the origin Bitmap is moved from right to left by the for loop, based on the
			// pieceWidth. In every step a snapshot as big as one piece is taken from the mc MovieClip. Each snapshot is stored in the 
			// returned Array.
			
			var pieceWidth:int = Math.round(int(_xml.Settings.@ImageWidth) / pieces);
			
			var pieceArray:Array = new Array();
			var bmd:BitmapData;
			var bm:Bitmap;
			
			var mc:MovieClip = new MovieClip();
			mc.addChild(origin);
			
			for (var i:int = 0; i < pieces; i++) {
				origin.x = i * -pieceWidth;
				
				if (i < pieces - 1) {
					bmd = new BitmapData(pieceWidth, int(_xml.Settings.@ImageHeight), false, 0xFFFFFFFF);
				}
				else {
					bmd = new BitmapData(int(_xml.Settings.@ImageWidth) - (pieceWidth * i), int(_xml.Settings.@ImageHeight), false, 0xFFFFFFFF);
				}
				
				bmd.draw(mc);
				bm = new Bitmap(bmd);
				
				pieceArray.push(bm);
			}
			
			return (pieceArray);
		}
		
		
		private function startTransition () {
			// This function starts the actual transition between two images.
			
			// Changing visibility from container and image
			container.visible = true;
			image.visible = false;
			
			// Setting up a rotation target, based on wether or not prevImage > nextImage.
			var rotationTarget:int = 90;
			if (prevImage > nextImage) {
				rotationTarget = -90;
			}
			
			// Getting the transition properties from the XML file. 
			var pieces:int = int(_xml.Transitions.Transition[transitionCount].@Pieces);
			var time:Number = Number(_xml.Transitions.Transition[transitionCount].@Time);
			var delay:Number = Number(_xml.Transitions.Transition[transitionCount].@Delay);
			var transition:String = String(_xml.Transitions.Transition[transitionCount].@Transition);
			var depthOffset:int = int(_xml.Transitions.Transition[transitionCount].@DepthOffset);
			var cubeDistance:int = int(_xml.Transitions.Transition[transitionCount].@CubeDistance);
			var cubeWidth:int = Math.round( int(_xml.Settings.@ImageWidth) / int(_xml.Transitions.Transition[transitionCount].@Pieces) );
			var cubeHeight:int = int(_xml.Settings.@ImageHeight);
			var darkness:Number = Number(_xml.Settings.@SideShadowAlpha);
			
			// Starting the tweens.
			for (var i:int = 0; i < cubes.length; i++) {
				// If the cube is turned backwards, the order of the sides needs to be changed.
				if (prevImage > nextImage) {
					cubes[i].swapChildrenAt(0, 2);
				}
				
				// Tweening the rotation of the cube over the whole transition time.
				Tweener.addTween (cubes[i], {rotationX:rotationTarget, time:time, delay:delay * i, transition:transition, onUpdate:checkRotation, onUpdateParams:[cubes[i]], onComplete:rotationComplete, onCompleteParams:[cubes[i]]});
				
				// Moving the cube backwards during the first 50% of the time and back forwards during the last 50% of the time.
				Tweener.addTween (cubes[i], {x:i * (cubeDistance + cubeWidth) - (cubeDistance * pieces) / 2, z:depthOffset + cubeHeight / 2, time:time / 2, delay:delay * i, transition:"easeInOutCubic"});
				Tweener.addTween (cubes[i], {x:i * cubeWidth, z:cubeHeight / 2, time:time / 2, delay:delay * i + time / 2, transition:"easeInOutCubic"});
				
				// Darkening the side width is not at the front at the beginning. Then tweening this side light. Also darkening the side, 
				// which is at the front at the beginning and moves back.
				Tweener.addTween (cubes[i].getChildAt(2), {_tintBrightness:-darkness, time:0});
				Tweener.addTween (cubes[i].getChildAt(2), {_tintBrightness:0, time:time, delay:delay * i, transition:transition});
				Tweener.addTween (cubes[i].getChildAt(4), {_tintBrightness:-darkness, time:time, delay:delay * i, transition:transition});
			}
			
			// Changing the transitionCount, so that the style of the transition can change for the next transition.
			transitionCount++;
			if (transitionCount == _xml.Transitions.Transition.length()) {
				transitionCount = 0;
			}
		}
		
		
		private function firstTransition () {
			// This function is more or less similar to the startTransition function, but for the first image, which is opened from
			// the background.
			
			var rotationTarget:int = 90;
			var pieces:int = 7;
			var time:Number = 0.6;
			var delay:Number = 0.03;
			var transition:String = "easeOutCubic";
			var cubeHeight:int = int(_xml.Settings.@ImageHeight);
			var darkness:Number = Number(_xml.Settings.@SideShadowAlpha);
			
			for (var i:int = 0; i < cubes.length; i++) {
				cubes[i].alpha = 0;
				cubes[i].z = 3000;
				dropshadow.getChildAt(i).alpha = 0;
				
				Tweener.addTween (cubes[i], {alpha:1, time:time, delay:delay * i});
				Tweener.addTween (dropshadow.getChildAt(i), {alpha:1, time:time, delay:delay * i});
				
				Tweener.addTween (cubes[i], {rotationX:rotationTarget, z:cubeHeight / 2, time:time, delay:delay * i, transition:transition, onUpdate:checkRotation, onUpdateParams:[cubes[i]], onComplete:rotationComplete, onCompleteParams:[cubes[i]]});
				
				Tweener.addTween (cubes[i].getChildAt(2), {_tintBrightness:-darkness, time:0});
				Tweener.addTween (cubes[i].getChildAt(2), {_tintBrightness:0, time:time, delay:delay * i, transition:transition});
				Tweener.addTween (cubes[i].getChildAt(4), {_tintBrightness:-darkness, time:time, delay:delay * i, transition:transition});
			}
		}
		
		
		private function checkRotation (cube:MovieClip) {
			// The transition of every cube needs to be monitored by an onUpdate handler of the tween. If 50% of the rotation is complete
			// the sides are swapped. Also the related part of the dropshadow is being moved accoring to the position of the cube.
			
			if (cube.rotationX > 45 && !cube.orderChanged || cube.rotationX < -45 && !cube.orderChanged) {
				cube.addChild(cube.getChildAt(3));
				cube.addChild(cube.getChildAt(2));
				cube.orderChanged = true;
			}
			dropshadow.getChildAt(cube.ID).x = cube.x;
			dropshadow.getChildAt(cube.ID).z = cube.z;
		}
		
		
		private function rotationComplete (cube:MovieClip) {
			// This function is being called every time a single cube is turned completely, but only if it's called by the very right cube, 
			// it actually does anything.
			
			if (cube.ID == cubes.length - 1) {
				// First the menu is being activated again, which was deactivated to make sure that no transition is started, while another
				// one is still going on.
				for (var i:int = 0; i < menu.numChildren; i++) {
					if (i != currentImage) {
						var mi:MovieClip = menu.getChildAt(i) as MovieClip;
						mi.button.addEventListener (MouseEvent.CLICK, menuClick);
					}
				}
				
				// The container including all cubes is made invisible.
				container.visible = false;
				
				// The image MovieClip is being created, made visible, positiones and added.
				image = new MovieClip();
				image.visible = true;
				image.x = container.x;
				image.y = container.y - Math.round(int(_xml.Settings.@ImageHeight) / 2) + 0.5;
				addChild(image);	
				
				// The actual image is being added to the image MovieClip. This could also be the preview image for SWF or video files.
				images[currentImage].x = 0;
				images[currentImage].y = 0;
				image.addChild(images[currentImage]);
				
				// imageMask is being applied.
				image.mask = imageMask;
				
				// If the current content is an etxernal SWF, a Loader is setup to load the SWF file.
				if (_xml.Contents.child(currentImage).name() == "Flash") {
					loadingMedia = currentImage;
					
					var loader:Loader = new Loader();
					loader.contentLoaderInfo.addEventListener (Event.COMPLETE, swfLoaded);
					loader.contentLoaderInfo.addEventListener (ProgressEvent.PROGRESS, swfLoading);
					loader.load(new URLRequest(_xml.Contents.child(currentImage).@Source));
				}
				
				// If the current content is a video file, the movie FLV Playback is added and the specified video file is assigned.
				// The movie is also positioned based on its size settings.
				if (_xml.Contents.child(currentImage).name() == "Video") {
					loadingMedia = currentImage;
					
					movie = new FLVPlayback();
					movie.source = _xml.Contents.child(currentImage).@Source;
					movie.addEventListener(VideoEvent.READY, movieReady);
					movie.addEventListener(VideoEvent.COMPLETE, mediaComplete);
					
					movie.width = int(_xml.Contents.child(currentImage).@Width);
					movie.height = int(_xml.Contents.child(currentImage).@Height);
					movie.x = Math.round((int(_xml.Settings.@ImageWidth) - movie.width) / 2);
					movie.y = Math.round((int(_xml.Settings.@ImageHeight) - movie.height) / 2);
					image.addChild(movie);
				}
				
				// The controls are being created depending on which type the content is.
				var controls:MovieClip = createControls();
				image.addChild(controls);
				
				// All single parts of the dropshadow are being removed.
				while (dropshadow.numChildren > 0) {
					dropshadow.removeChildAt(0);
				}
				
				// Instead one single surface Sprite is being created and applied.
				var s:Sprite = new Sprite();
				s.z = int(_xml.Settings.@ImageHeight) / 2;
				s.graphics.beginFill(0x000000, 1);
				s.graphics.drawRect(0, 0, int(_xml.Settings.@ImageWidth), int(_xml.Settings.@ImageHeight));
				s.graphics.endFill();
				s.rotationX = 90;	
				dropshadow.addChild(s);
				
				// If autoplay in on, at tween is applied to the countdown variable, which is actually counted up to 360. Based on this
				// value the countdown in the menu is being illustrated and finally a new transition is started at the end of the tween.
				if (int(_xml.Settings.@Autoplay) > 0 && isPlaying && _xml.Contents.child(currentImage).name() == "Image") {
					countdown = 0;
					Tweener.addTween (this, {countdown:360, time:int(_xml.Settings.@Autoplay), transition:"linear", onUpdate:countdownUpdate, onComplete:countdownComplete});
				}
			}
		}
		
		
		private function movieReady (e:VideoEvent) {
			// This function starts a loaded video file, if autoplay is set true for this very video in the XML.
			if (loadingMedia == currentImage) {
				if (_xml.Contents.child(currentImage).@Autoplay == "true") {
					movie.play();
				}
				else {
					movie.stop();
				}
				controls.visible = true;
			}
		}
		
		
		private function swfLoading (e:ProgressEvent) {
			// If you have very large SWF files to be loaded externally, you might want to include an internal preloader. This would be 
			// the place to do so.
		}
		
		
		private function swfLoaded (e:Event) {
			// This function is called, when an etxernal SWF file is fully loaded. It adds the SWF to the image MovieClip.
			if (loadingMedia == currentImage) {
				image.addChild(e.target.content);
				e.target.content.addEventListener (Event.COMPLETE, mediaComplete);
			}
		}
		
		
		private function mediaComplete (e:*) {
			// This function is called, when a media (video or external SWF) is complete. For a video, this is the case, when the video
			// is played to the very end. For an SWF this is teh case, when the SWF dispatches a COMPLETE Event 
			if (int(_xml.Settings.@Autoplay) > 0 && isPlaying && _xml.Contents.child(currentImage).name() == "Flash") {
				countdown = 0;
				Tweener.addTween (this, {countdown:360, time:int(_xml.Settings.@Autoplay), transition:"linear", onUpdate:countdownUpdate, onComplete:countdownComplete});
			}
			else if (int(_xml.Settings.@Autoplay) > 0 && isPlaying && _xml.Contents.child(currentImage).name() == "Video") {
				countdownComplete();
			}
			else if (!isPlaying && _xml.Contents.child(currentImage).name() == "Video") {
				MovieClip(controls.getChildAt(0)).playMC.visible = true;
				MovieClip(controls.getChildAt(0)).stopMC.visible = false;
			}
		}
		
		
		private function createControls():MovieClip {
			// This function creates the control elements (PLAY, STOP, LINK, INFO) every time a new content is opened. Which control
			// elements are included, depends on the type of content and the existence of hyperlinks or info texts. It returns a MovieClip
			// containing the different control elements.
			controls = new MovieClip();
			controls.alpha = 0;
			
			// Setting up the controls, if the current content is an Image.
			if (_xml.Contents.child(currentImage).name() == "Image") {
				if (_xml.Contents.child(currentImage).Text != undefined && _xml.Contents.child(currentImage).Text != "") {
					var imageInfo:MovieClip = setupControl("info");
					imageInfo.button.addEventListener(MouseEvent.CLICK, showInfo);
					controls.addChild(imageInfo);
				}
				if (int(_xml.Settings.@Autoplay) > 0) {
					var autoplay:MovieClip;
					if (isPlaying) {
						autoplay = setupControl("stop");
					}
					else {
						autoplay = setupControl("play");
					}
					autoplay.x = controls.numChildren * (int(_xml.Settings.@ControlSize) + int(_xml.Settings.@ControlDistance));
					autoplay.button.addEventListener(MouseEvent.CLICK, startStopAutoplay);
					controls.addChild(autoplay);
				}
				if (_xml.Contents.child(currentImage).Hyperlink.@URL != undefined && _xml.Contents.child(currentImage).Hyperlink.@URL != "") {
					var href:MovieClip = setupControl("link");
					href.button.addEventListener(MouseEvent.CLICK, hyperlink);
					href.x = controls.numChildren * (int(_xml.Settings.@ControlSize) + int(_xml.Settings.@ControlDistance));
					controls.addChild(href);
				}
			}
			
			// Setting up the controls, if the current content is an Video
			else if (_xml.Contents.child(currentImage).name() == "Video") {
				var playPause:MovieClip;
				if (_xml.Contents.child(currentImage).@Autoplay == "true") {
					playPause = setupControl("stop");
				}
				else {
					playPause = setupControl("play");
				}
				playPause.x = controls.numChildren * (int(_xml.Settings.@ControlSize) + int(_xml.Settings.@ControlDistance));
				playPause.button.addEventListener(MouseEvent.CLICK, videoPlayPause);
				controls.addChild(playPause);
				
				controls.visible = false;
			}
			
			// Positioning the controls according to the settings from the XML
			controls.x = int(_xml.Settings.@ControlsX);
			controls.y = int(_xml.Settings.@ControlsY);
			if (_xml.Settings.@ControlsAlign == "center") {
				controls.x -= Math.round(controls.width / 2 - int(_xml.Settings.@ControlSize) / 2);
			}
			else if (_xml.Settings.@ControlsAlign == "left") {
				controls.x += Math.round(int(_xml.Settings.@ControlSize) / 2);
			}
			else if (_xml.Settings.@ControlsAlign == "right") {
				controls.x -= Math.round(controls.width - int(_xml.Settings.@ControlSize) / 2);
			}
			
			// Showing the controls initially, if the mouse cursor is on the image
			var bounds:Rectangle = image.getBounds(this);
			if (mouseX > bounds.x && mouseX < bounds.right && mouseY > bounds.y && mouseY < bounds.bottom) {
				showControls (new Event("controlsCreated"));
			}
			
			// Adding the listeners to show or hide the controls
			image.addEventListener(MouseEvent.ROLL_OVER, showControls);
			image.addEventListener(MouseEvent.ROLL_OUT, hideControls);
			
			return (controls);
		}
		
		
		private function setupControl (cType:String):MovieClip {
			// This function sets up a control element (PLAY, STOP, LINK, INFO) with its basic appearance and functionality.
			var c:MovieClip = new Control();
			
			c.alpha = Number(_xml.Settings.@ControlAlpha);
			c.width = int(_xml.Settings.@ControlSize);
			c.height = int(_xml.Settings.@ControlSize);
			
			c.stopMC.visible = false;
			c.playMC.visible = false;
			c.infoMC.visible = false;
			c.linkMC.visible = false;
			c.getChildByName(cType + "MC").visible = true;
			
			Tweener.addTween (c.bg, {_color:int(_xml.Settings.@ControlColor1)});
			Tweener.addTween (c.stopMC, {_color:int(_xml.Settings.@ControlColor2)});
			Tweener.addTween (c.infoMC, {_color:int(_xml.Settings.@ControlColor2)});
			Tweener.addTween (c.playMC, {_color:int(_xml.Settings.@ControlColor2)});
			Tweener.addTween (c.linkMC, {_color:int(_xml.Settings.@ControlColor2)});
			
			c.button.addEventListener (MouseEvent.ROLL_OVER, controlOver);
			c.button.addEventListener (MouseEvent.ROLL_OUT, controlOut);
			
			return (c);
		}
		
		
		private function showControls (e:*) {
			// This function makes the controls visible.
			Tweener.addTween (controls, {alpha:1, time:0.6, transition:"easeOutQuad"});
		}
		
		
		private function hideControls (e:*) {
			// This function makes the controls invisible.
			Tweener.addTween (controls, {alpha:0, time:0.6, transition:"easeOutSine"});
		}
		
		
		private function controlOver (e:MouseEvent) {
			// Highlight a control on RollOver
			Tweener.addTween (e.target.parent, {alpha:Number(_xml.Settings.@ControlAlphaOver), time:0.3});
		}
		
		
		private function controlOut (e:MouseEvent) {
			// Remove highlight from control on RollOut
			Tweener.addTween (e.target.parent, {alpha:Number(_xml.Settings.@ControlAlpha), time:0.4, transition:"linear"});
		}
		
		
		private function videoPlayPause (e:MouseEvent) {
			// Starting a video content, if is was stopped before - and vice versa
			if (movie.playing) {
				movie.pause();
				MovieClip(e.target.parent).playMC.visible = true;
				MovieClip(e.target.parent).stopMC.visible = false;
			}
			else {
				movie.play();
				MovieClip(e.target.parent).playMC.visible = false;
				MovieClip(e.target.parent).stopMC.visible = true;
			}
		}
		
		
		private function startStopAutoplay (e:MouseEvent) {
			// This function activates / deactivates the autoplay functionality. It sets the isPlaying variable, starts or removes a
			// tween of the countdown variable and also sets up the controls and the menu accordingly.
			
			if (isPlaying) {
				isPlaying = false;
				
				Tweener.removeTweens (this);
				var mi:MovieClip = menu.getChildAt(currentImage) as MovieClip;
				MovieClip(mi.countdownMC).graphics.clear();
				
				MovieClip(e.target.parent).playMC.visible = true;
				MovieClip(e.target.parent).stopMC.visible = false;
			}
			
			else {
				isPlaying = true;
				
				prevImage = currentImage;
				nextImage = currentImage + 1;
				if (nextImage == images.length) {
					nextImage = 0;
				}
				currentImage = nextImage;
				
				changeImage();
			}
		}
		
		
		private function showInfo (e:MouseEvent) {
			// This function shows the info text. Therefor it turns the center part of the image.
			// First the controls are hidden. As soon as this is done, the sliceAndTurn function is started.
			Tweener.addTween (controls, {alpha:0, time:0.2, onComplete:sliceAndTurn});
			image.removeEventListener(MouseEvent.ROLL_OVER, showControls);
			image.removeEventListener(MouseEvent.ROLL_OUT, hideControls);
			
			// The container is made empty.
			while (container.numChildren > 0) {
				container.removeChildAt(0);
			}
			
			// If autoplay is active, the countdown is stopped and also the countdown visualization is cleared.
			if (isPlaying) {
				Tweener.removeTweens (this);
				var mi:MovieClip = menu.getChildAt(currentImage) as MovieClip;
				MovieClip(mi.countdownMC).graphics.clear();
			}
			
			function sliceAndTurn () {
				// Then the actual sliceAndTurn is started. This function works pretty much like the transition between two images. 
				// Therefor it's not described in depth. Basically three cubes are created by slicing the current image. The center cube
				// is turned. The back of the cube contains a TextField with the info text. All this is done within the container 
				// MovieClip, while the image MovieClip is made invisile. Also a listener is added, to hide the info part on RollOut.
				var left:MovieClip = new MovieClip();
				var right:MovieClip = new MovieClip();
				var center:MovieClip = new MovieClip();
				
				var bottom:Sprite = new Sprite();
				bottom.graphics.beginFill(0x00FF00, 0);
				bottom.graphics.drawRect(0, int(_xml.Settings.@ImageHeight) / -2, int(_xml.Settings.@ImageWidth), int(_xml.Settings.@ImageHeight));
				bottom.graphics.endFill();
				container.addChild(bottom);
				
				var bmd:BitmapData;
				
				bmd = new BitmapData (int(_xml.Settings.@ImageWidth), int(_xml.Settings.@ImageHeight) );
				bmd.draw (images[currentImage]);
				var origin:Bitmap = new Bitmap(bmd);
				
				var mc:MovieClip = new MovieClip();
				mc.addChild(origin);
				
				bmd = new BitmapData( Math.round( (int(_xml.Settings.@ImageWidth) - int(_xml.Settings.@InfoWidth)) / 2 ), int(_xml.Settings.@ImageHeight) );
				bmd.draw (mc);
				var leftBM:Bitmap = new Bitmap(bmd);
				left.y = Math.round( int(_xml.Settings.@ImageHeight) / -2 )
				left.addChild(leftBM);
				container.addChild(left);
				
				bmd = new BitmapData( Math.round( (int(_xml.Settings.@ImageWidth) - int(_xml.Settings.@InfoWidth)) / 2 ), int(_xml.Settings.@ImageHeight) );
				origin.x = left.width - int(_xml.Settings.@ImageWidth);
				bmd.draw (mc);
				var rightBM:Bitmap = new Bitmap(bmd);
				right.x = int(_xml.Settings.@ImageWidth) - left.width;
				right.y = Math.round( int(_xml.Settings.@ImageHeight) / -2 )
				right.addChild(rightBM);
				container.addChild(right);
				
				center.x = left.width;
				center.z = int(_xml.Settings.@ImageHeight) / 2;
				
				bmd = new BitmapData( int(_xml.Settings.@ImageWidth) - left.width * 2, int(_xml.Settings.@ImageHeight) );
				origin.x = left.width * -1;
				bmd.draw (mc);
				var centerBM:Bitmap = new Bitmap(bmd);
				
				var bmd2:BitmapData = bmd.clone();
				var infoBM:Bitmap = new Bitmap(bmd2);
				
				var front:MovieClip = new MovieClip();
				front.name = "front";
				front.rotationX = 0;
				var fSurface:MovieClip = new MovieClip();
				fSurface.y = int(_xml.Settings.@ImageHeight) / -2;
				fSurface.z = int(_xml.Settings.@ImageHeight) / -2;
				fSurface.addChild(centerBM);
				front.addChild(fSurface);
				
				var back:MovieClip = new MovieClip();
				back.name = "back";
				back.rotationX = -90;
				var bSurface:MovieClip = new MovieClip();
				bSurface.y = int(_xml.Settings.@ImageHeight) / -2;
				bSurface.z = int(_xml.Settings.@ImageHeight) / -2;
				bSurface.addChild(infoBM);
				var overlay:Sprite = new Sprite();
				overlay.graphics.beginFill(int(_xml.Settings.@InfoBackground), Number(_xml.Settings.@InfoBackgroundAlpha));
				overlay.graphics.drawRect(0, 0, bSurface.width, bSurface.height);
				overlay.graphics.endFill();
				bSurface.addChild(overlay);
				back.addChild(bSurface);
				
				var tf:TextField = new TextField();
				tf.embedFonts = true;
				tf.antiAliasType = AntiAliasType.ADVANCED;
				tf.sharpness = int(_xml.Settings.@InfoSharpness);
				tf.thickness = int(_xml.Settings.@InfoThickness);
				tf.multiline = true;
				tf.wordWrap = true;
				tf.styleSheet = css;
				tf.htmlText = CSSManager.includeMargins(_xml.Contents.child(currentImage).Text, css);
				tf.width = int(_xml.Settings.@InfoWidth) - int(_xml.Settings.@InfoMargin) * 2;
				tf.height = int(_xml.Settings.@ImageHeight) - int(_xml.Settings.@InfoMargin) * 2;
				tf.x = int(_xml.Settings.@InfoMargin);
				tf.y = int(_xml.Settings.@InfoMargin);
				bSurface.addChild(tf);
				
				for (var i:int = 0; i < 2; i++) {
					var inner:Sprite = new Sprite();
					inner.graphics.beginFill(int(_xml.Settings.@InnerSideColor), 1);
					inner.graphics.drawRect(0, 0, int(_xml.Settings.@ImageHeight), int(_xml.Settings.@ImageHeight));
					inner.graphics.endFill();
					inner.rotationY = -90;
					if (i == 0) {
						inner.x = left.width;
						left.addChild(inner);
					}
					else {
						right.addChild(inner);
					}
				}
				
				center.addChild(back);
				center.addChild(front);
				container.addChild(center);
				
				center.scaleX = front.width / ( front.width + 1 );
				center.scaleY = front.height / ( front.height + 1 );
				center.scaleZ = front.height / ( front.height + 1 );
				
				var time:Number = 0.8;
				Tweener.addTween (center, {rotationX:90, time:time, transition:"easeOutCubic", onUpdate:infoUpdate, onUpdateParams:[center]});
				Tweener.addTween (center, {z:int(_xml.Settings.@ImageHeight) / 2 + 100, time:time / 2, transition:"easeInOutCubic"});
				Tweener.addTween (center, {z:int(_xml.Settings.@ImageHeight) / 2, time:time / 2, delay:time / 2, transition:"easeInOutCubic"});
				Tweener.addTween (back, {_tintBrightness:-Number(_xml.Settings.@SideShadowAlpha)});
				Tweener.addTween (back, {_tintBrightness:0, time:time, transition:"easeOutCubic"});
				Tweener.addTween (front, {_tintBrightness:-Number(_xml.Settings.@SideShadowAlpha), time:time, transition:"easeOutCubic"});
				
				image.visible = false;
				container.visible = true;
				
				container.addEventListener(MouseEvent.ROLL_OUT, closeInfo);
			}
		}
		
		
		private function infoUpdate (mc:MovieClip) {
			// This function monitors the process of the turn to the info text. It's rather similar to checkRotation.
			var front:MovieClip = mc.getChildByName("front") as MovieClip;
			var back:MovieClip = mc.getChildByName("back") as MovieClip;
			
			if (mc.rotationX > 45 && mc.getChildIndex(front) > mc.getChildIndex(back)) {
				mc.addChildAt(front, mc.getChildIndex(back));
			}
			
			if (mc.rotationX < 45 && mc.getChildIndex(back) > mc.getChildIndex(front)) {
				mc.addChildAt(back, mc.getChildIndex(front));
			}
		}
		
		
		private function closeInfo (e:MouseEvent) {
			// This function closes the info text again on RollOut. Tweens are similar to startTransition.
			
			container.removeEventListener(MouseEvent.ROLL_OUT, closeInfo);
			var mc:MovieClip = container.getChildAt(3) as MovieClip;
			
			var time:Number = 0.8;
			Tweener.addTween (mc, {rotationX:0, time:time, transition:"easeOutCubic", onUpdate:infoUpdate, onUpdateParams:[mc], onComplete:infoClosed});
			Tweener.addTween (mc, {z:int(_xml.Settings.@ImageHeight) / 2 + 100, time:time / 2, transition:"easeInOutCubic"});
			Tweener.addTween (mc, {z:int(_xml.Settings.@ImageHeight) / 2, time:time / 2, delay:time / 2, transition:"easeInOutCubic"});
			Tweener.addTween (mc.getChildByName("front"), {_tintBrightness:0, time:0.7, transition:"easeOutCubic"});
			Tweener.addTween (mc.getChildByName("back"), {_tintBrightness:-Number(_xml.Settings.@SideShadowAlpha), time:0.7, transition:"easeOutCubic"});
			
			// If autoplay was active before the info text was shown, it's restarted here.
			if (isPlaying) {
				countdown = 0;
				Tweener.addTween (this, {countdown:360, time:int(_xml.Settings.@Autoplay), transition:"linear", onUpdate:countdownUpdate, onComplete:countdownComplete});
			}
		}
		
		
		private function infoClosed () {
			// When the info is closed entirely, the container MovieClip is made invisible again and the image MovieClip visible.
			// Also controls are installed again.
			
			image.visible = true;
			container.visible = false;
			
			image.addEventListener(MouseEvent.ROLL_OVER, showControls);
			image.addEventListener(MouseEvent.ROLL_OUT, hideControls);
			
			var bounds:Rectangle = image.getBounds(this);
			if (mouseX > bounds.x && mouseX < bounds.right && mouseY > bounds.y && mouseY < bounds.bottom) {
				showControls (new Event("backFromInfo"));
			}
		}
		
		
		private function hyperlink (e:MouseEvent) {
			// This function implements an hyperlink on images. It's called, when the link button is clicked.
			var req:URLRequest = new URLRequest (_xml.Contents.child(currentImage).Hyperlink.@URL);
			navigateToURL (req, _xml.Contents.child(currentImage).Hyperlink.@Target);
		}
		
		
		private function menuSetup (mi:MovieClip) {
			// This function initially sets up a menu item. It sets the colors and fades it in slightly.
			var xTarg:int = mi.x;
			
			mi.colorMC.width = 0;
			mi.colorMC.height = 0;
			
			Tweener.addTween (mi.circleMC, {_color:int(_xml.Settings.@MenuColor1)});
			Tweener.addTween (mi.colorMC, {_color:int(_xml.Settings.@MenuColor3)});
			
			mi.x = xTarg + 30;
			Tweener.addTween (mi, {x:xTarg, alpha:1, time:0.4, delay:menu.getChildIndex(mi) * 0.1});
		}
		
		
		private function menuOver (e:MouseEvent) {
			// On RollOver on menu items this function is called. It enlarges the menu item slightly and opens the tooltip for this item.
			
			var mi:MovieClip = e.target.parent as MovieClip;
			if (menu.getChildIndex(mi) != currentImage) {
				Tweener.addTween (mi.maskMC, {width:12, height:12, time:0.3});
			}
			
			if (_xml.Contents.child(menu.getChildIndex(mi)).@Title != undefined && _xml.Contents.child(menu.getChildIndex(mi)).@Title != "") {
				createTooltip(menu.getChildIndex(mi));
				if (tooltipTimer != null) {
					tooltipTimer.stop();
				}
			}
			else {
				removeTooltip(new Event("emptyTooltip"))
			}
		}
		
		
		private function menuOut (e:MouseEvent) {
			// This function sets the menu item back to normal. Also it starts a Timer to remove the tooltip after 0.3 seconds, as long
			// as no other menu item is hovered in the meantime.
			
			var mi:MovieClip = e.target.parent as MovieClip;
			if (menu.getChildIndex(mi) != currentImage) {
				Tweener.addTween (mi.maskMC, {width:10, height:10, time:0.6});
			}
			
			tooltipTimer = new Timer(300, 1);
			tooltipTimer.addEventListener(TimerEvent.TIMER_COMPLETE, removeTooltip);
			tooltipTimer.start();
		}
		
		
		private function menuClick (e:MouseEvent) {
			// This function is called, when a menu item is clicked. It sets up the properties for the transition to the related content.
			prevImage = currentImage;
			nextImage = menu.getChildIndex(e.target.parent);
			currentImage = nextImage;
			
			changeImage();
			
			removeTooltip (new Event("clicked"));
		}
		
		
		private function createTooltip (ID:int) {
			// A tooltip is created.
			// If it's not already there (which happens when you hover from one tooltip to another), it fades in.
			if (tooltip.alpha < 1) {
				Tweener.addTween (tooltip, {alpha:1, time:0.4, transition:"easeOutQuad"});
			}
			
			// A new TextField is set up based on the settings of the XML.
			var tf:TextField = new TextField();
			tf.embedFonts = true;
			tf.multiline = false;
			tf.selectable = false;
			tf.autoSize = TextFieldAutoSize.LEFT;
			tf.antiAliasType = AntiAliasType.ADVANCED;
			tf.sharpness = int(_xml.Settings.@TooltipTextSharpness);
			tf.thickness = int(_xml.Settings.@TooltipTextThickness);
			tf.defaultTextFormat = tooltipFormat;
			tf.y = tooltip.ttText.numChildren * int(_xml.Settings.@TooltipHeight) + int(_xml.Settings.@TooltipTextY);
			tf.text = _xml.Contents.child(ID).@Title;
			tf.x = Math.round(tf.width / -2);
			tooltip.ttText.addChild(tf);
			
			// Depending on whether or not the tooltip was visible already, it's setup immediately or changed in 0.5 seconds.
			var time:Number = 0;
			if (tooltip.alpha > 0) {
				time = 0.5;
			}
			
			// These tweens change/setup the tooltip position, the text and the width both of the tooltip surface and the mask.
			Tweener.addTween (tooltip, {x:menu.x + menu.getChildAt(ID).x, time:time});
			Tweener.addTween (tooltip.ttText, {y:tooltip.ttText.numChildren * int(_xml.Settings.@TooltipHeight) * -1, time:time, transition:"easeOutCubic"});
			Tweener.addTween (tooltip.ttSurface, {width:Math.ceil(tf.width), time:time});
			Tweener.addTween (tooltip.ttMask, {width:Math.ceil(tf.width), time:time});
			
			tooltip.visible = true;
			addChild(tooltip);
		}
		
		
		private function removeTooltip (e:*) {
			// The tooltip is faded out.
			Tweener.addTween (tooltip, {alpha:0, time:0.4, transition:"linear", onComplete:tooltipRemoved});
		}
		
		
		private function tooltipRemoved () {
			// When the tooltip is removed, all TextFields are removed. Also visibility is set to false.
			tooltip.visible = false;
			while (tooltip.ttText.numChildren > 0) {
				tooltip.ttText.removeChildAt(0);
			}
		}
		
		
		private function menuActivation (mi:MovieClip) {
			// This function activates a menu item, as soon as the image for the related content is fully loaded. It enlarges the menu
			// item slightly and also adds the listeners.
			Tweener.addTween (mi.maskMC, {width:10, height:10, time:0.4});
			
			mi.button.buttonMode = true;
			mi.button.addEventListener (MouseEvent.CLICK, menuClick);
			mi.button.addEventListener (MouseEvent.ROLL_OVER, menuOver);
			mi.button.addEventListener (MouseEvent.ROLL_OUT, menuOut);
			
			// If the loaded image is the first image, the visual appearence of the menu item is changed to active and also the CLICK
			// listener is removed again.
			if (menu.getChildIndex(mi) == 0) {
				Tweener.removeTweens (mi.circleMC);
				Tweener.addTween (mi.colorMC, {width:7, height:7, time:0.5});
				Tweener.addTween (mi.circleMC, {_color:int(_xml.Settings.@MenuColor2), time:0.5, delay:0.5, transition:"linear"});
				Tweener.addTween (mi.maskMC, {width:14, height:14, time:0.5});
				
				mi.button.removeEventListener (MouseEvent.CLICK, menuClick);
			}
		}
		
		
		private function countdownUpdate () {
			// This function draws the graphical visualization of the countdown into the current menu item.
			var mi:MovieClip = menu.getChildAt(currentImage) as MovieClip;
			var c:MovieClip = mi.countdownMC as MovieClip;
			var rad:int = 20;
			
			c.graphics.clear();
			c.graphics.beginFill (int(_xml.Settings.@MenuColor1), 1);
			c.graphics.moveTo (0, 0);
			c.graphics.lineTo(0, -rad);
			if (countdown > 0) {c.graphics.lineTo(rad, -rad)};
			if (countdown > 90) {c.graphics.lineTo(rad, rad)};
			if (countdown > 180) {c.graphics.lineTo(-rad, rad)};
			if (countdown > 270) {c.graphics.lineTo(-rad, -rad)};
			c.graphics.lineTo( Math.sin(countdown * 2 * Math.PI / 360) * rad, -Math.cos(countdown * 2 * Math.PI / 360) * rad);
			c.graphics.lineTo(0, 0);
			c.graphics.endFill();
		}
		
		
		private function countdownComplete () {
			// This function starts a transition to the next content, when the autoplay countdown is complete.			
			prevImage = currentImage;
			nextImage = currentImage + 1;
			if (nextImage == images.length) {
				nextImage = 0;
			}
			currentImage = nextImage;
			
			changeImage();
		}
		
		
		private function resetShadow (e:Event) {
			// For some reason the dropshadow can occasionally disappear in the browser. This seems to solve the issue.
			// It's more of a dirty fix though. :)
			for (var i:int = 0; i < dropshadow.numChildren; i++) {
				dropshadow.getChildAt(i).scaleY = 0.5;
				dropshadow.getChildAt(i).scaleY = 1;
			}
		}
		
		
		private function stageResize (e:Event) {
			// This keeps the projection center centered to the Piecemaker
			root.transform.perspectiveProjection.projectionCenter = new Point(this.x, this.y);
		}
		
		
		private function removedFromStage (e:Event) {
			// When the Piecemaker is removed from the stage, some functionality has to be deactivated. If a video is playing, it's stopped
			// just after the audio has been faded out. Also the autoplay is stopped by removing all Tweens from the Piecemaker.
			Tweener.removeTweens(this);
			isPlaying = false;
			if (movie != null) {
				Tweener.addTween (movie, {volume:0, time:1, transition:"linear", onComplete:function(){movie.stop()} });
			}
		}
		
		
	}
	
}