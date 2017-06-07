var url = document.URL;
var articleId = url.substring(url.lastIndexOf('/') + 1);
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
				var $partDown = $(".part_down");
				$partDown.attr("id", ar.articleId);
				$partDown.find(".ar_date").html(ar.createDate);
				$partDown.find(".ar_title").find("label").html(ar.title);
				$partDown.find(".ar_time").find("label").html(ar.createTime);
				$partDown.find("#readNum").html(ar.attr.readNum);
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
var $coPromise = $.ajax({
	cache : false,
	type : "get",
	async : true,
	url : '/api/v1/article/' + articleId + "/newComments",
	success : function(data) {
		var obj = jQuery.parseJSON(data);
		if (obj.code == '0') {
			var ar = obj.data;
			if (ar != null) {
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
				var $partDown = $(".part_down");
				$partDown.find(".ar_date").html(ar.createDate);
				$partDown.find(".ar_title").find("label").html(ar.title);
				$partDown.find(".ar_time").find("label").html(ar.createTime);
				$partDown.find("#readNum").html(ar.attr.readNum);
				$partDown.find(".ar_content").attr("id", ar.articleId);
				$partDown.find(".ar_content").html(ar.content);
			}
		} else {
			return false;
		}
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
			content : '评论不能空哦',
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
								+ '<label class="item_p_username">'
								+ jsonObj.data.replyerName
								+ '</label>'
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

			$(".co_btn").on('click', function() {
				comment();
			});

		});