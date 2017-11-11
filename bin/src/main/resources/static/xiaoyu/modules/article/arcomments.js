var url = document.URL;
var articleId = url.split('/')[4];
var $coPromise = $
		.ajax({
			cache : false,
			type : "get",
			async : true,
			url : '/api/v1/article/' + articleId + "/comments",
			data : {
				pageNum : 1
			},
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
						var $html = '<div class="co_num"><span>全部评论</span><span>('
								+ obj.count + ')</span></div>';
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
											$html += '<div class="item_down"><label class="item_p_title_pure"><a id="'
													+ co.replyerId
													+ '">回复</a></label>';
											$html += '<label class="item_p_title_pure">'
													+ co.createDate
													+ '</label></div></div>';

										});
						$html += '</div>';
						$coComment.html($html);
					}
				}
				$(".icon_like").on(
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

$(document).ready(function() {
	$("#login").bind("click", function() {
		gotoLogin('/article/' + articleId + "/comments");
	});
	$coPromise.promise().done(function() {
		addHeadForImg();
	});
});