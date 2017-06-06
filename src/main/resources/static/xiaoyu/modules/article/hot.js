var $ajaxPromise = $
		.ajax({
			type : "get",
			async : true,
			url : '/api/v1/article/hot',
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
					if (obj.data != null || obj.data.len >= 0) {
						var arHtml = '<div class="item_list"  >';
						$
								.each(
										obj.data,
										function(index2, ar) {
											var childHtml = '<dl>';

											childHtml += '<div class="list_item">';
											childHtml += '<span class="item_userinfo" ><img class="avatar small" img-type="avatar" src="'
													+ ar.user.avatar
													+ '" id="'
													+ ar.user.userId + '"/>';
											childHtml += '<p class="item_desc">'
													+ ar.user.description
													+ '</p>';
											childHtml += '</span>';
											childHtml += '<dt class="item_username">'
													+ ar.user.nickname
													+ '</dt>';
											childHtml += '<div class="item_ar" id="'
													+ ar.articleId + '">';
											childHtml += '<dt class="item_ar_title">'
													+ ar.title + '</dt>';
											childHtml += '<p class="item_ar_content">'
													+ ar.content + '</p>';
											childHtml += ' </div>';

											childHtml += '<div class="comment_bar"><div class="bar_part">';
											if (ar.isLike == "1") {
												childHtml += '<i class="icon_like" style="color:#ff4949;" data-like="1"></i>';
											} else {
												childHtml += '<i class="icon_like" data-like="0"></i>';
											}

											childHtml += '<label style="margin: 2px;">'
													+ ar.attr.likeNum
													+ '</label></div>';
											childHtml += '<div class="bar_part"><i class="icon_comment_alt"></i><label style="margin: 2px;">'
													+ ar.attr.commentNum
													+ '</label></div>';
											childHtml += '<div class="bar_part">';
											if (ar.isCollect == "1") {
												childHtml += '<i class="icon_heart_alt" style="color:#ff4949;" data-heart="1"></i>';
											} else {
												childHtml += '<i class="icon_heart_alt" data-heart="0"></i>';
											}
											childHtml += '<label style="margin: 2px;">'
													+ ar.attr.collectNum
													+ '</label></div>';
											childHtml += '</div>';
											childHtml += '</li>';
											childHtml += ' </div>';
											childHtml += '</dl>';
											arHtml += childHtml;
										});
						arHtml += '</div>';
						$(".list_hot").append(arHtml);

					}
				}
				$(".icon_heart_alt").on(
						"click",
						function() {
							var $userInfo = jQuery.parseJSON($.session
									.get("user"));
							if (checkNull($userInfo)) {
								window.location.href = "/login";
								return false;
							}

							var $icon = $(this);
							var elem = $icon.parent().parent().parent();
							var $ar = elem.find(".item_ar");
							var $next = $icon.next();
							var num = $next.html();
							var $isCollect;
							if ($icon.attr('data-heart') == '0') {
								$icon.css("color", "#ff4949");
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
									articleId : $ar.attr("id"),
									isCollect : $isCollect
								},
								beforeSend : function(xhr) {

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
						});
				$(".icon_comment_alt").on(
						"click",
						function() {
							var $icon = $(this);
							var elem = $icon.parent().parent().parent();
							var $ar = elem.find(".item_ar");
							window.location.href = "/article/" + $ar.attr("id")
									+ "/comments"

						});
				$(".icon_like").on(
						"click",
						function() {
							var $icon = $(this);
							var elem = $icon.parent().parent().parent();
							var $ar = elem.find(".item_ar");
							var $next = $icon.next();
							var num = $next.html();
							var $isLike;
							if ($icon.attr('data-like') == '0') {
								$icon.css("color", "#ff4949");
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
									articleId : $ar.attr("id"),
									isLike : $isLike
								},
								beforeSend : function(xhr) {
									var userInfo = jQuery.parseJSON($.session
											.get("user"));
									if (!checkNull(userInfo)) {
										xhr.setRequestHeader('token',
												userInfo.token);
										xhr.setRequestHeader('userId',
												userInfo.userId);
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
				addHeadForImg();
				$('.item_ar').bind('click', function() {
					var $elem = $(this);
					window.location.href = "/article/" + $elem.attr('id');
				});
				$('.avatar').bind('click', function() {
					var $elem = $(this);
					window.location.href = "/user/" + $elem.attr('id');
				});
				return true;
			}
		});

var userInfo = jQuery.parseJSON($.session.get('user'));

$(document).ready(
		function() {

			$ajaxPromise.promise().done(function() {

				if (!isPC()) {
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