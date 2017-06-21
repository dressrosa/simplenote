var url = document.URL;
var userId = url.substring(url.lastIndexOf('/') + 1);

var $ajaxPromise1 = $.ajax({
	type : "get",
	async : true,
	url : '/api/v1/user/' + userId,
	success : function(data) {
		var obj = jQuery.parseJSON(data);
		if (obj.code == "0") {
			var $user = obj.data;
			if (!checkNull($user)) {
				setTitle($user.nickname + '-个人中心');
				var $userPanel = $(".panel-default");
				if (checkNull($user.avatar)) {
					$user.avatar = 'common/avatar.png';
				}
				$userPanel.find("img").attr("src", $user.avatar);
				$userPanel.find("img").attr("id", $user.userId);
				$userPanel.find(".nickname_panel").html($user.nickname);
				$userPanel.find(".des_panel").html($user.description);

			}
		} else {
			window.location.href = "/common/404";
		}

	}
});
var before = function(xhr) {
	var userInfo = jQuery.parseJSON($.session.get("user"));
	if (!checkNull(userInfo)) {
		xhr.setRequestHeader('token', userInfo.token);
		xhr.setRequestHeader('userId', userInfo.userId);
	}
};
var handleAll = function(data) {
	var obj = jQuery.parseJSON(data);
	if (obj.code == '0') {
		var arHtml = "";
		if (obj.data != null || obj.data.len >= 0) {
			$
					.each(
							obj.data,
							function(index, ar) {
								arHtml += '<li class="list-group-item"   id="'
										+ ar.articleId + '">';
								arHtml += '<label>' + ar.title + '</label>';
								arHtml += '<p class="group_item_p">'
										+ ar.content + '...' + '</p>';

								arHtml += '<div class="comment_bar"><div class="bar_part">';
								if (ar.isLike == "1") {
									arHtml += '<i class="icon_like" style="color:#fd4d4d;" data-like="1"></i>';
								} else {
									arHtml += '<i class="icon_like" data-like="0"></i>';
								}

								arHtml += '<label style="margin: 2px;">'
										+ ar.attr.likeNum + '</label></div>';
								arHtml += '<div class="bar_part">';
								arHtml += '<i class="icon_comment_alt"></i>';
								arHtml += '<label style="margin: 2px;">'
										+ ar.attr.commentNum + '</label></div>';
								arHtml += '<div class="bar_part">';
								if (ar.isCollect == "1") {
									arHtml += '<i class="icon_heart_alt" style="color:#fd4d4d;" data-heart="1"></i>';
								} else {
									arHtml += '<i class="icon_heart_alt" data-heart="0"></i>';
								}
								arHtml += '<label style="margin: 2px;">'
										+ ar.attr.collectNum + '</label></div>';
								arHtml += '</div>';
								arHtml += '</li>';

							});
			$(".list-group").html(arHtml);
			$(".list-group").attr("id", "list-all");
			$.session.set("pr-al-0", arHtml, 10 * 60);
		}
	}

	addHeadForImg();
	return true;

};

var handleCollected = function(data) {

	var obj = jQuery.parseJSON(data);
	if (obj.code == '0') {
		var arHtml = "";
		if (obj.data != null || obj.data.len >= 0) {
			$
					.each(
							obj.data,
							function(index, ar) {
								arHtml += '<li class="list-group-item"   id="'
										+ ar.articleId + '">';
								arHtml += '<label>' + ar.title + '</label>';
								arHtml += '<p class="group_item_p">'
										+ ar.content + '...' + '</p>';

								arHtml += '<div class="comment_bar"><div class="bar_part">';
								if (ar.isLike == "1") {
									arHtml += '<i class="icon_like" style="color:#fd4d4d;" data-like="1"></i>';
								} else {
									arHtml += '<i class="icon_like" data-like="0"></i>';
								}

								arHtml += '<label style="margin: 2px;">'
										+ ar.attr.likeNum + '</label></div>';
								arHtml += '<div class="bar_part">';
								arHtml += '<i class="icon_comment_alt"></i>';
								arHtml += '<label style="margin: 2px;">'
										+ ar.attr.commentNum + '</label></div>';
								arHtml += '<div class="bar_part">';
								if (ar.isCollect == "1") {
									arHtml += '<i class="icon_heart_alt" style="color:#fd4d4d;" data-heart="1"></i>';
								} else {
									arHtml += '<i class="icon_heart_alt" data-heart="0"></i>';
								}
								arHtml += '<label style="margin: 2px;">'
										+ ar.attr.collectNum + '</label></div>';
								arHtml += '</div>';
								arHtml += '</li>';

							});
			if ($(".list-group").attr("id") == "list-collected") {
				$(".list-group").html(arHtml);
			}
			$.session.set("pr-cd-1", arHtml, 10 * 60);

		}
	}

	addHeadForImg();
	return true;

};
var $all = $.ajax({
	type : "get",
	async : true,
	url : '/api/v1/article/list',
	data : {
		userId : userId
	},
	beforeSend : function(xhr) {
		return before(xhr);
	},
	success : function(data) {
		return handleAll(data);
	}
});

var $collected = $.ajax({
	type : "get",
	async : true,
	url : '/api/v1/article/list/collect',
	data : {
		userId : userId
	},
	beforeSend : function(xhr) {
		return before(xhr);
	},
	success : function(data) {
		return handleCollected(data);
	}
});
var removeAllCache = function() {
	$.session.remove("pr-al-0");
	$.session.remove("pr-cd-1");
};
$(document).ready(function() {
	$ajaxPromise1.promise().done(function() {
	});
	$all.promise().done(function() {

	});
	$collected.promise().done(function() {

	});
	// tab page
	$(".tab_ul").on('click', 'li', function() {
		var $selected = $(this);
		$.each($selected.siblings(), function(i, v) {
			$(v).removeClass('li_active');
		});
		$selected.addClass('li_active');
		switch ($selected.attr('data-select')) {
		case '0':
			var $ck_al = $.session.get("pr-al-0");
			if (!checkNull($ck_al)&&$ck_al !='null') {
				$(".list-group").html($ck_al);
			} else {
				$.ajax({
					type : "get",
					async : true,
					url : '/api/v1/article/list',
					data : {
						userId : userId
					},
					beforeSend : function(xhr) {
						return before(xhr);
					},
					success : function(data) {
						return handleAll(data);
					}
				});

			}
			$(".list-group").attr("id", "list-all");
			break;
		case '1':
			var $pr_cd = $.session.get("pr-cd-1");
			if (!checkNull($pr_cd)&& $pr_cd !='null') {
				$(".list-group").html($pr_cd);
			} else {
				$.ajax({
					type : "get",
					async : true,
					url : '/api/v1/article/list/collect',
					data : {
						userId : userId
					},
					beforeSend : function(xhr) {
						return before(xhr);

					},
					success : function(data) {
						return handleCollected(data);
					}

				});

			}
			$(".list-group").attr("id", "list-collected");
			break;
		case '2':
			$(".list-group").html("2");
			break;
		}
	});

	$(".list-group").delegate(".icon_comment_alt", "click", function() {
		var $icon = $(this);
		var elem = $icon.parent().parent().parent();
		window.location.href = "/article/" + elem.attr("id") + "/comments"

	});
	$(".list-group").delegate(".icon_like", "click", function() {
		var elem = $(this).parent().parent().parent();
		var $icon = $(this);
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
			url : '/api/v1/article/like',
			data : {
				articleId : elem.attr("id"),
				isLike : $isLike
			},
			beforeSend : function(xhr) {
				return before(xhr);

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
		removeAllCache();
	});

	$(".list-group").delegate("p", "click", function() {
		var $icon = $(this);
		var elem = $icon.parent();
		window.location.href = '/article/' + elem.attr("id");
	})

	$(".list-group").delegate(".icon_heart_alt", "click", function() {
		var $userInfo = jQuery.parseJSON($.session.get("user"));
		if (checkNull($userInfo)) {
			window.location.href = "/login";
			return false;
		}
		var $icon = $(this);
		var elem = $icon.parent().parent().parent();
		var $next = $icon.next();
		var num = $next.html();
		var $isCollect;
		if ($icon.attr('data-heart') == '0') {
			$icon.css("color", "#fd4d4d");
			$icon.attr("data-heart", "1");
			$next.html(num - (-1));
			$isCollect = 0;
		} else if ($icon.attr('data-heart') == '1') {
			$icon.css("color", "#a7a7a7");
			$icon.attr("data-heart", "0");
			$next.html(num - 1);
			$isCollect = 1;
		}
		$.ajax({
			type : "post",
			async : true,
			url : '/api/v1/article/collect',
			data : {
				articleId : elem.attr("id"),
				isCollect : $isCollect
			},
			beforeSend : function(xhr) {
				return before(xhr);
			},
			success : function(data) {
				console.log(data);
				var obj = jQuery.parseJSON(data);
				if (obj.code == "20001") {
					console.log("未登录");
					window.location.href = "/login";
					return false;
				}
				return true;
			},
			error : function(data) {
				console.log(data);
				return false;
			}
		});
		removeAllCache();
	});
	if (!isPC()) {
		$(".main").css("width", "100%");
		$(".content").css("width", "100%");
		$(".content").css("display", "block");
		$(".content-left").css("width", "100%");
		$(".content-right").css("width", "100%");
	}
	$("#login").bind("click", function() {
		gotoLogin('/user/' + userId);
	});
	var $userInfo = jQuery.parseJSON($.session.get("user"));
	var $thisUserId = userId;
	if (!checkNull($userInfo)) {
		if ($userInfo.userId == userId) {
			$(".camera").css("display", "block");
			$(".avatar_wrapper").on("click", function() {
				window.location.href = "/user/" + userId + "/edit";
			});
		}
	}
});