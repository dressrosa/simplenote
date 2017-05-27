 
	var $ajaxPromise =
		$.ajax({
		type : "get",
		async : true,
		url : '/api/v1/article/hot',
		success : function(data) {
			var obj = jQuery.parseJSON(data);
			if (obj.code == '0') {
				if (obj.data != null || obj.data.len >= 0) {
					$.each(obj.data,function(index1, child) {
										var arHtml  = '<div class="col-xs-3" style="margin-top: 20px;">';
										$.each(child, function(index2, ar) {
											var childHtml ='<dl>';
											if((index1*3+index2)%2==0) {
												childHtml+='<div class="sponsor">';
												childHtml+= '<div class="sponsorFlip" id="'+ar.articleId+'" name="'+ar.user.userId+'" lang="forArticle">';
												childHtml+='<p>'+ar.content+'</p>';
												childHtml+='<div class="g-line" style="width: 25%; margin: 25px auto;"></div></div>';
												
												childHtml+='<div class="sponsorData">';
												childHtml+='<span><img src="'+ar.user.avatar+'" /></span>';
												childHtml+='<dt>'+ar.user.nickname+'</dt>';
												childHtml+='<p>'+ar.user.description +'</p>';
												childHtml+='<div class="g-line" style="width: 25%; margin: 25px auto;"></div></div>';
												childHtml+='</div>';
											}
											else {
												childHtml+='<div class="sponsor">';
												childHtml+= '<div class="sponsorFlip" id="'+ar.user.userId+'" name="'+ar.articleId+'" lang="forUser">';
												childHtml+='<span><img src="'+ar.user.avatar+'" /></span>';
												childHtml+='<dt>'+ar.user.nickname+'</dt>';
												childHtml+='<p>'+ar.user.description +'</p>';
												childHtml+='<div class="g-line" style="width: 25%; margin: 25px auto;"></div></div>';
												
												childHtml+='<div class="sponsorData">';
												childHtml+='<dt>'+ar.title+'</dt>';
												childHtml+='<p>'+ar.content+'</p>';
												childHtml+='<div class="g-line" style="width: 25%; margin: 25px auto;"></div></div>';
												childHtml+='</div>';
											}
											childHtml+='</dl>';
											arHtml+=childHtml;
										});
										arHtml+='</div>';
										$(".hot").append(arHtml);
									});
					
				}
			}
			 addHeadForImg();
			 $('.sponsorFlip').bind('click', function() {
					var elem = $(this);
					console.log(elem.attr('lang'));
					if ('forUser' == elem.attr('lang')) {
						window.location.href = "/user/" + elem.attr('id');
						
					} else {
						window.location.href = "/article/" + elem.attr('id');
						
					}

				});
			return true;
		}
	});
 
 

var userInfo = jQuery.parseJSON($.session.get('user'));

$(document).ready(
		function() {
			 
			$ajaxPromise.promise().done(function(){
				$(".col-xs-3").sortable({
					connectWith : '.col-xs-3',// 级联其他
					containment : "document",// 作用范围
					delay : 150,// 延迟效果
					revert : true
					// 动画
				});
				 $(".col-xs-3").disableSelection();
				 if (!isPC()) {
					$(".col-xs-3").css("width", "100%");
					$(".hot").css("display", "block");
					$(".hot").css("width", "100%");
					$("#footer").css("display", "none");
				}
			});

			if (checkNull(userInfo)) {
				$("#loginSpan").css("display", "block");
			} else {
				$("#userSpan").css("display", "block");
				$("#userSpan").find("#nickname").attr("href",
						"/user/" + userInfo.userId);
				$("#userSpan").find("#nickname").text(userInfo.nickname);
			}
		});