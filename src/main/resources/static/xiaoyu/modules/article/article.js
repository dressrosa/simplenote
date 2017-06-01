var url = document.URL;
var articleId = url.substring(url.lastIndexOf('/'));
var $ajaxPromise = $.ajax({
	cache : false,
	type : "get",
	async : true,
	url : '/api/v1/article' + articleId,
	success : function(data) {
		var obj = jQuery.parseJSON(data);
		if (obj.code == '0') {
			var ar = obj.data;
			if (ar != null) {
				$(".part_up").find("img").attr('src', ar.user.avatar);
				$(".p_description").find("span").html(ar.user.description);
				$(".p_username").find(".nickname").html(ar.user.nickname);
				var $partDown = $(".part_down");
				$partDown.find(".ar_date").html(ar.createDate);
				$partDown.find(".ar_time").find("label").html(ar.createTime);
				$partDown.find("#readNum").html(ar.readNum);
				$partDown.find(".ar_content").attr("id", ar.articleId);
				$partDown.find(".ar_content").html(ar.content);

			}
		} else {
			// window.location.href = "/common/404";
			return false;
		}
		addHeadForImg();
		return true;
	}
});

function publish() {
	var tip = "";
	var userInfo = jQuery.parseJSON($.session.get("user"));
	if (checkNull(userInfo)) {
		tip = "登录后再来写写吧";
		$('.tooltip').jBox('Tooltip', {
			content : tip,
			pointer : false,
			animation : 'zoomIn',
			closeOnClick : 'body',
			target : $(".btn")
		}).open();
		return false;
	}
	var content = $("#articleContent");
	if (checkNull(content.val())) {
		tip = "不如先写几个字吧"
		$('.tooltip').jBox('Tooltip', {
			content : tip,
			pointer : false,
			animation : 'zoomIn',
			closeOnClick : 'body',
			target : $(".btn")
		}).open();
		return false;
	}
	var userId = userInfo.userId;
	var token = userInfo.token;
	$.ajax({
		type : "post",
		url : "/api/v1/article/add",
		data : {
			userId : userId,
			token : token,
			content : content.val()
		},
		async : true,
		error : function(data) {
			tip = "没成功,是不是没网啊"
			$('.tooltip').jBox('Tooltip', {
				content : tip,
				pointer : false,
				animation : 'zoomIn',
				closeOnClick : 'body',
				target : $(".btn")
			}).open();
			return false;
		},
		success : function(data) {
			var jsonObj = jQuery.parseJSON(data);
			if (jsonObj.code == '0') {
				window.location.href = "/article/" + jsonObj.data;
			}
			if (jsonObj.code = '20001') {
				$.session.remove('user');
				$('.tooltip').jBox('Tooltip', {
					content : jsonObj.message,
					pointer : false,
					animation : 'zoomIn',
					closeOnClick : 'body',
					target : $(".btn")
				}).open();

			} else {
				$('.tooltip').jBox('Tooltip', {
					content : jsonObj.message,
					pointer : false,
					animation : 'zoomIn',
					closeOnClick : 'body',
					target : $(".btn")
				}).open();
			}
			return true;
		}
	});
}
$(document).ready(
		function() {
			$("#login").bind("click", function() {
				gotoLogin('/article/' + articleId);
			});
			$ajaxPromise.promise()
					.done(
							function() {
								var item = $(".ar_content").attr("id");
								$.ajax({
									type : 'POST',
									async : true,
									url : '/api/v1/article/viewNum/' + item,
									error : function(data) {
										console.log(data);
									},
									success : function(data) {
										console.log(data);
									}
								});
								//comment
								$(".part_comment").bind("click",function(){
									window.location.href = "/article/comments";
								});
								
								$arContent = $(".ar_content");

								console.log($arContent.css("height") + ","
										+ $arContent.css("max-height"))
								if ($arContent.css("height") >= $arContent
										.css("max-height")) {
									$arContent.find(".ar_more").css("display",
											"block");

								}
								if (!isPC()) {
									$(".main").css("display", "block");
									$(".siderbar_left").css("display", "flex");
									var $shadow = $(".img_shadow");
									var img = $(".img_shadow").find("img")
									$shadow.find("img").css({
										"border-radius" : "50%",
										"width" : "100px",
										"height" : "100px"
									});
								}
							});
			$("#publish").bind("click", function() {
				publish();
			});

		});