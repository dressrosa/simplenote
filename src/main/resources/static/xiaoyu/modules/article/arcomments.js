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
											$html += '<div class="co_item"><div class="item_up"><div style="margin-top: -30px; margin-left: -10px;">	<img img-type="avatar " class="avatar small" src="'
													+ co.replyerAvatar
													+ '"></div><div class="item_p"><label class="item_p_username" >'
													+ co.replyerName
													+ '</label>';
											if (!checkNull(co.parentReplyerName)) {
												$html += '<label class="item_p_label">回复</label> <label class="item_p_username">'
														+ co.parentReplyerName
														+ '</label>';
											}

											$html += '<p>'
													+ co.content
													+ '</p></div><div class="item_like"><i class="icon_like"></i><label class="co_item_label">0</label></div></div><div class="item_down"><label class="item_p_title_pure"><a id="'
													+ co.replyerId
													+ '">回复</a></label><label class="item_p_title_pure">'
													+ co.createDate
													+ '</label></div></div>';
										});
						$html += '</div>';
						$coComment.html($html);
					}
				}

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