var images = [ {
	text : '海贼王1',
	src : 'http://xiaoyu1-1253813687.costj.myqcloud.com/common/1.jpg'
}, {
	text : '海贼王2',
	src : 'http://xiaoyu1-1253813687.costj.myqcloud.com/common/2.jpg'
},  {
	text : '海贼王3',
	src : 'http://xiaoyu1-1253813687.costj.myqcloud.com/common/3.png'
},  {
	text : '海贼王4',
	src : 'http://xiaoyu1-1253813687.costj.myqcloud.com/common/4.jpg'
},];
var interval = 6000;
var duration_image = 800;
var duration_text = 300;
var mobile = (/android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i
		.test(navigator.userAgent.toLowerCase()));
var current = Math.floor(Math.random() * (images.length + 1));
var loading = false;
var timer;
function showImage(index) {
	if (loading)
		return;
	index == 'next' && (index = current + 1);
	(index > images.length - 1) && (index = 0);
	var next = index + 1;
	(next > images.length - 1) && (next = 0);
	$('#image' + current).animate({
		opacity : 0
	}, {
		duration : duration_image,
		queue : false
	});
	$('#text' + current).animate({
		opacity : 0
	}, {
		duration : duration_text,
		queue : false
	});
	if (!$('#image' + index).length) {
		$('#slideshow-images').addClass('spinner');
		loading = true;
		$('<img src="' + images[index].src + '">').load(
				function() {
					$('#slideshow-images').removeClass('spinner');
					$('<div/>').addClass('slideshow-image').attr('id',
							'image' + index).css({
						opacity : 0,
						backgroundImage : 'url(' + images[index].src + ')'
					}).appendTo($('#slideshow-images'));
					$('<div/>').addClass('slideshow-text').attr('id',
							'text' + index).html(images[index].text).css({
						opacity : 0
					}).appendTo($('#slideshow-texts'));
					loading = false;
					showImage(index);
				});
	} else {
		$('#image' + index).animate({
			opacity : 1
		}, {
			duration : duration_image,
			queue : false
		});
		$('#text' + index).animate({
			opacity : 1
		}, {
			duration : duration_text,
			queue : false
		});
		if (!$('#image' + next).length) {
			loading = true;
			$('<img src="' + images[next].src + '">').load(
					function() {
						$('<div/>').addClass('slideshow-image').attr('id',
								'image' + next).css({
							opacity : 0,
							backgroundImage : 'url(' + images[next].src + ')'
						}).appendTo($('#slideshow-images'));
						$('<div/>').addClass('slideshow-text').attr('id',
								'text' + next).html(images[next].text).css({
							opacity : 0
						}).appendTo($('#slideshow-texts'));
						loading = false;
					});
		}
		current = index;
	}
}
function startSlideshow() {
	if (!mobile)
		timer = setInterval(function() {
			showImage('next');
		}, interval);
}
function stopSlideshow() {
	clearInterval(timer);
}
function responsiveSlideshow() {
	$('#slideshow-images').css('height', jQuery(window).width() * (330 / 800));
}
$(document).ready(function() {
	$('#slideshow-info').mouseenter(function() {
		$('#slideshow-texts').addClass('active');
	}).mouseleave(function() {
		$('#slideshow-texts').removeClass('active');
	});
	$('#slideshow-images').click(function() {
		stopSlideshow();
		showImage('next');
	});
	responsiveSlideshow();
	startSlideshow();
	showImage(current);
});
$(window).resize(function() {
	responsiveSlideshow();
});