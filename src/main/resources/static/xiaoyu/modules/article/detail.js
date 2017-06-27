var url = document.URL;
var articleId = url.split('/')[4];

var before = function(xhr) {
	var userInfo = jQuery.parseJSON($.session.get("user"));
	if (!checkNull(userInfo)) {
		xhr.setRequestHeader('token', userInfo.token);
		xhr.setRequestHeader('userId', userInfo.userId);
	}
};
// is loved
var userInfo = jQuery.parseJSON($.session.get("user"));
if (!checkNull(userInfo)) {
	$.ajax({
		cache : false,
		type : "get",
		async : true,
		url : '/api/v1/article/isLoved' + articleId,
		beforedSend:function(xhr) {
			xhr.setRequestHeader('token', userInfo.token);
			xhr.setRequestHeader('userId', userInfo.userId);
		},
		success : function(data) {
			console.log(data);
			var obj =  jQuery.parseJSON(data);
			if(obj.code == 0) {
				if(obj.data.isLove='1') {
					$(".p_love").text("取消关注");
					$(".p_love").css({
						"background":"#e2e2e2"
					});
				}
					 
				
			}
			else if(obj.code='20001') {
				
			}
		}
		
	});
}

var $arPromise = $.ajax({
	cache : false,
	type : "get",
	async : true,
	url : '/api/v1/article/' + articleId,
	success : function(data) {
		var obj = jQuery.parseJSON(data);
		if (obj.code == '0') {
			var ar = obj.data;
			if (ar != null) {
				setTitle('详情-' + ar.title);
				var $partUp = $(".part_up");
				$partUp.find("img").attr('src', ar.user.avatar);
				$partUp.find("img").on("click", function() {
					window.location.href = "/user/" + ar.user.userId;
				});
				$partUp.find(".p_description").find("span").html(
						ar.user.description);
				$partUp.find(".p_username").find(".nickname").html(
						ar.user.nickname);
				
				$partUp.find(".red").find("label").html(ar.attr.likeNum);
				$partUp.find(".blue").find("label").html(ar.attr.collectNum);
				$partUp.find(".green").find("label").html(ar.attr.commentNum);
				var $co = $partUp.find(".p_comment");
				$co.find("label").html('<a>'+ar.user.nickname+':'+'</a>');
				$co.find("p").html(ar.user.description);
				$("#co_title").html('作者简介');
				var $partDown = $(".part_down");
				$partDown.attr("id", ar.articleId);
				$partDown.find(".ar_date").html(ar.createDate);
				$partDown.find(".ar_title").find("h2").html(ar.title);
				$partDown.find(".ar_time").find("label").html(ar.createTime);
				$partDown.find(".ar_view").html('浏览量:' + ar.attr.readNum);
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
var $coPromise = $
		.ajax({
			cache : false,
			type : "get",
			async : true,
			url : '/api/v1/article/' + articleId + "/newComments",
			beforeSend : function(xhr) {
				var $userInfo = jQuery.parseJSON($.session.get("user"));
				if (!checkNull($userInfo)) {
					xhr.setRequestHeader('token', $userInfo.token);
					xhr.setRequestHeader('userId', $userInfo.userId);
				}

			},
			success : function(data) {
				var obj = jQuery.parseJSON(data);
				if (obj.code == '0') {
					var $coList = obj.data;
					if (!checkNull($coList)) {
						var $coComment = $(".co_comment");
						var $html = '<div class="co_num"><span>最新评论</span></div>';
						$html += '<div class="co_list">';
						$
								.each(
										$coList,
										function(index, co) {
											$html += '<div class="co_item" data-id="'
													+ co.commentId
													+ '"><div class="item_up"><div style="margin-top: -30px; margin-left: -10px;">	<img img-type="avatar " class="avatar small" src="'
													+ co.replyerAvatar
													+ '"></div><div class="item_p"><label class="item_p_username"><a>'
													+ co.replyerName
													+ '</a></label>';
											if (!checkNull(co.parentReplyerName)) {
												$html += '<label class="item_p_label">回复</label> <label class="item_p_username">'
														+ co.parentReplyerName
														+ '</label>';
											}

											$html += '<p>' + co.content
													+ '</p></div>';
											if (co.isLike == "1") {
												$html += '<div class="item_like"><i class="icon_like" style="color:#fd4d4d;"  data-like="1"></i><label class="co_item_label">'
														+ co.num
														+ '</label></div>';
											} else {
												$html += '<div class="item_like"><i class="icon_like"  data-like="0"></i><label class="co_item_label">'
														+ co.num
														+ '</label></div>';
											}
											$html += '</div>';
											$html += '<div class="item_down"><label class="item_p_title_pure">'
													+ co.createDate
													+ '</label></div></div>';
										});
						$html += '</div>';
						$html += '<div class="co_all"><span><a href="/article/'
								+ articleId
								+ '/comments">查看全部</a></span></div>';
						$coComment.html($html);
					}
				}
				$(".co_comment .icon_like").on(
						"click",
						function() {
							var $icon = $(this);
							var elem = $icon.parent().parent().parent();
							var $next = $icon.next();
							var num = $next.html();
							var $isLike;
							if ($icon.attr('data-like') == '0') {
								$icon.css("color", "#fd4d4d");
								$icon.attr("data-like", "1");
								$next.html(num - (-1));
								$isLike = 0;
							} else if ($icon.attr('data-like') == '1') {
								$icon.css("color", "#a7a7a7");
								$icon.attr("data-like", "0");
								$next.html(num - 1);
								$isLike = 1;
							}
							$.ajax({
								type : "post",
								async : true,
								url : '/api/v1/article/comments/like',
								data : {
									commentId : elem.attr("data-id"),
									isLike : $isLike
								},
								beforeSend : function(xhr) {
									var $userInfo = jQuery.parseJSON($.session
											.get("user"));
									if (!checkNull($userInfo)) {
										xhr.setRequestHeader('token',
												$userInfo.token);
										xhr.setRequestHeader('userId',
												$userInfo.userId);
									}

								},
								success : function(data) {
									console.log(data);
									var obj = jQuery.parseJSON(data);
									if (obj.code == "20001") {
										console.log("未登录");
									}
									return true;
								},
								error : function(data) {
									console.log(data);
									return false;
								}
							});
						});
				return true;
			}
		});

var comment = function() {
	var $userInfo = jQuery.parseJSON($.session.get("user"));
	if (checkNull($userInfo)) {
		window.location.href = "/login";
		return false;
	}
	var $text = $(".co_tt");
	if (checkNull($text.val())) {
		new jBox('Tooltip', {
			content : '评论不能空',
			pointer : false,
			animation : 'zoomIn',
			closeOnClick : 'body',
			target : $(".co_tt")
		}).open();
		return false;
	}
	if ($text.val().length > 100) {
		new jBox('Tooltip', {
			content : '评论字数仅限100以内',
			pointer : false,
			animation : 'zoomIn',
			closeOnClick : 'body',
			target : $(".co_tt")
		}).open();
		return false;
	}
	$(".co_btn").attr("disabled", "disabled");
	$
			.ajax({
				type : 'post',
				async : true,
				url : '/api/v1/article/' + $(".part_down").attr("id")
						+ "/comment",
				data : {
					content : $(".co_tt").val()
				},
				beforeSend : function(xhr) {
					if (!checkNull($userInfo)) {
						xhr.setRequestHeader('token', $userInfo.token);
						xhr.setRequestHeader('userId', $userInfo.userId);
					}
				},
				error : function(data) {
					console.log(data);
					var jsonObj = jQuery.parseJSON(data);
					$(".co_btn").removeAttr("disabled");
					return false;
				},
				success : function(data) {
					var jsonObj = jQuery.parseJSON(data);
					if (jsonObj.code == '0') {
						new jBox('Tooltip', {
							content : '评论成功',
							pointer : false,
							animation : 'zoomIn',
							closeOnClick : 'body',
							target : $(".co_tt")
						}).open();
						$(".co_tt").val("");
						var $coItem = '<div class="co_item"><div class="item_up">'
								+ '<div style="margin-top: -30px; margin-left: -10px;">'
								+ '	<img img-type="avatar " class="avatar small" src="'
								+ imgHead
								+ jsonObj.data.replyerAvatar
								+ '">'
								+ '</div>'
								+ '<div class="item_p">'
								+ '<label class="item_p_username"><a>'
								+ jsonObj.data.replyerName
								+ '</a></label>'
								+ '<p>'
								+ jsonObj.data.content
								+ '</p>'
								+ '</div><div class="item_like"><i class="icon_like"></i>'
								+ '<label class="co_item_label">0</label>'
								+ '</div></div>'
								+ '<div class="item_down">'
								+ '<label class="item_p_title_pure">'
								+ jsonObj.data.createDate
								+ '</label>'
								+ '</div></div>';
						$(".co_list").prepend($coItem);
					} else if (jsonObj.code == '20001') {
						gotoLogin('/article/' + articleId);
					}
					$(".co_btn").removeAttr("disabled");

					return true;
				}

			});

};
$(document).ready(
		function() {
			$("#login").bind("click", function() {
				gotoLogin('/article/' + articleId);
			});
			$arPromise.promise()
					.done(
							function() {
								var item = $(".ar_content").attr("id");
								$.ajax({
									type : 'POST',
									async : true,
									url : '/api/v1/article/viewNum/' + item,
									error : function(data) {
										console.log(data);
										return false;
									},
									success : function(data) {
										console.log(data);
										return true;
									}
								});
								// comment
								$(".part_comment").bind("click", function() {
									// window.location.href =
									// "/article/comments";
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

			$coPromise.promise().done(function() {
				addHeadForImg();
			});

			$(".co_btn").on('click', function() {
				comment();
			});

		});