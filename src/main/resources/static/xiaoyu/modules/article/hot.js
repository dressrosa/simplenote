var $ajaxPromise = $
		.ajax({
			type : "get",
			async : true,
			url : '/api/v1/article/hot',
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
											childHtml += '<span class="item_userinfo" id="'
													+ ar.user.userId
													+ '"><img class="avatar small" img-type="avatar" src="'
													+ ar.user.avatar + '" />';
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
											childHtml += ' </div>';

											childHtml += '<div class="comment_bar"><div class="bar_part">';
											if ('0'== "1") {
												childHtml += '<i class="icon_like" style="color:#ff4949d9;" data-like="1"></i>';
											} else {
												childHtml += '<i class="icon_like" data-like="0"></i>';
											}

											childHtml += '<label style="margin: 2px;">'
													+ 0
													+ '</label></div>';
											childHtml += '<div class="bar_part"><i class="icon_comment_alt"></i><label style="margin: 2px;">'
													+ 0
													+ '</label></div>';
											childHtml += '<div class="bar_part"><i class="icon_heart_alt"></i><label style="margin: 2px;">'
													+ 0
													+ '</label></div>';
											childHtml += '</div>';
											childHtml += '</li>';
											
											childHtml += '</dl>';
											arHtml += childHtml;
										});
						arHtml += '</div>';
						$(".list_hot").append(arHtml);

					}
				}
				addHeadForImg();
				$('.item_ar').bind('click', function() {
					var $elem = $(this);
					window.location.href = "/article/" + $elem.attr('id');
				});
				$('.item_userinfo').bind('click', function() {
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