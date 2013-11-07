$(document).ready(function(){
  //Activate the Poshytips ------------------------------------------------------ //
    $('.poshytip').poshytip({
    	className: 'tip-twitter',
		showTimeout: 1,
		alignTo: 'target',
		alignX: 'center',
		alignY:'bottom',
		offsetY: 10,
		allowTipHover: false
    });
	 $('.poshytip1').poshytip({
    	className: 'tip-twitter',
		showTimeout: 1,
		alignTo: 'target',
		alignX: 'center',
		alignY:'top',
		offsetY: 10,
		allowTipHover: false
    });
//Activate the Twitter
$(function(){
      $('#tweets').tweetable({username: 'anariel77', time: true, limit: 1, replies: true, position: 'append'});
  });
  	$(window).load(function(){
				$('#hovereffect').hoverwords({ delay:50 });
				$('#hovereffect1').hoverwords({ delay:50 });
	})
	// Activate the Nivo slider	
$(window).load(function() {
    $('#slider').nivoSlider({
    });
});
 
 //Activate the Piecemaker 
    var flashvars = {};
  flashvars.xmlSource = "piecemaker.xml";
  flashvars.cssSource = "piecemaker.css";
  var params = {};
  params.play = "true";
  params.menu = "false";
  params.scale = "showall";
  params.wmode = "transparent";
  params.allowfullscreen = "true";
  params.allowscriptaccess = "always";
  params.allownetworking = "all";
  
  swfobject.embedSWF('piecemaker.swf', 'piecemaker', '960', '300', '10', null, flashvars,    
  params, null);
  
  // Activate the prettyPhoto
 $(document).ready(function(){
    $("a[rel^='prettyPhoto']").prettyPhoto();
  });

  // Hover effect --------------------------------------------- //
	
		$('div#portfolio2columns img,div#portfolioFirstrow img,div#portfolioSecondrow img,div#portfolioThirdrow img,.advertising ul li img,div.post img').hover(function(){
		// on rollover
		$(this).stop().animate({ 
			opacity: "0.7"
		}, "fast");
	} , function() { 
		// on out
		$(this).stop().animate({
			opacity: "1" 
		}, "fast");
	});
  
  // Activate the toggle-content on click
$('.toggle-content').hide();
    $('.toggle-title').click(function(){
        $(this).next().slideToggle(300);
        $(this).children('span.open-toggle').toggleClass('closed');
        $(this).attr('title', ($(this).attr('title') == 'Close') ? 'Open' : 'Close');
        return false;
    });
		   	
	// Activate the contactform
$(function(){
	$('#contact_form').submit(function(e){
		e.preventDefault();
		var form = $(this);
		var post_url = form.attr('action');
		var post_data = form.serialize();
		$('#loader', form).html('<img src="img/loader.gif" /> Please Wait...');
		$.ajax({
			type: 'POST',
			url: post_url, 
			data: post_data,
			success: function(msg) {
				$(form).fadeOut(500, function(){
					form.html(msg).fadeIn();
				});
			}
		});
	});
});  	
	});
	// Activate the MainMenu

			  $(document).ready(function(){ 
				  $("ul.sf-menu").superfish(); 
			  }); 
	
