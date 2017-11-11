var userInfo = jQuery.parseJSON($.session.get("user"));
var $msg = $.ajax({
	type : "get",
	async : true,
	url : '/api/v1/message/type/0',
	data : {
		userId : userInfo.userId
	},
	beforeSend : function(xhr) {
		var $u = jQuery.parseJSON($.session.get("user"));
		xhr.setRequestHeader('pageNum', 1);
		if (!checkNull(userInfo)) {
			xhr.setRequestHeader('token', $u.token);
			xhr.setRequestHeader('userId', $u.userId);
		}
	},
	success : function(data) {
		var obj = jQuery.parseJSON(data);
		if (obj.code == '0' && !checkNull(obj.data)) {
			return handleMsg(0, obj.data)
		} else {
			$(".msg_list").html(blankPage);
		}
	}
});
var $note = $.ajax({
	type : "get",
	async : true,
	url : '/api/v1/message/type/1',
	data : {
		userId : userInfo.userId
	},
	beforeSend : function(xhr) {
		var $u = jQuery.parseJSON($.session.get("user"));
		xhr.setRequestHeader('pageNum', 1);
		if (!checkNull(userInfo)) {
			xhr.setRequestHeader('token', $u.token);
			xhr.setRequestHeader('userId', $u.userId);
		}
	},
	success : function(data) {
		var obj = jQuery.parseJSON(data);
		if (obj.code == '0' && !checkNull(obj.data)) {
			return handleMsg(1, obj.data)
		} else {
			$(".msg_list").html(blankPage);
		}
	}
});
var $notice = $.ajax({
	type : "get",
	async : true,
	url : '/api/v1/message/type/2',
	data : {
		userId : userInfo.userId
	},
	beforeSend : function(xhr) {
		var $u = jQuery.parseJSON($.session.get("user"));
		xhr.setRequestHeader('pageNum', 1);
		if (!checkNull(userInfo)) {
			xhr.setRequestHeader('token', $u.token);
			xhr.setRequestHeader('userId', $u.userId);
		}
	},
	success : function(data) {
		var obj = jQuery.parseJSON(data);
		if (obj.code == '0' && !checkNull(obj.data)) {
			return handleMsg(2, obj.data)
		} else {
			$(".msg_list").html(blankPage);
		}
	}
});
var handleMsg = function(type, data) {
	var $html = '';
	switch (type) {
	case 0:
		$
				.each(
						data,
						function(index, m) {
							var $mHtml = '<div class="msg_body" id="'
									+ m.messageId + '">';
							$mHtml += '<div>';
							if (m.isRead == 0) {
								$mHtml += '<i class="icon_chat_alt red"></i>';
							} else {
								$mHtml += '<i class="icon_chat_alt gray"></i>';
							}

							$mHtml += '<label>' + m.createDate + '</label>';
							$mHtml += '</div>';
							$mHtml += '<div>';
							$mHtml += '<p>' + m.content + '</p>';
							$mHtml += '</div>';
							$mHtml += '<div class="font_small">';
							$mHtml += '<label style="float: right;"><a>回复</a>&nbsp;<a>删除</a></label>';
							$mHtml += '</div>';
							$mHtml += '</div>';
							$html += $mHtml;
						});
		break;
	case 1:
		$
				.each(
						data,
						function(index, m) {
							var $mHtml = '<div class="msg_body">';
							$mHtml += '<div>';
							if (m.isRead == 0) {
								$mHtml += '<i class="icon_book_alt red"></i>';
							} else {
								$mHtml += '<i class="icon_book_alt gray"></i>';
							}
							$mHtml += '<label>' + m.createDate + '</label>';
							$mHtml += '</div>';
							$mHtml += '<div>';
							$mHtml += '<p>' + m.content + '</p>';
							$mHtml += '</div>';
							$mHtml += '<div class="font_small">';
							$mHtml += '<label style="float: right;"><a>回复</a>&nbsp;<a>删除</a></label>';
							$mHtml += '</div>';
							$mHtml += '</div>';
							$html += $mHtml;
						});
		break;
	case 2:
		$
				.each(
						data,
						function(index, m) {
							var $mHtml = '<div class="msg_body">';
							$mHtml += '<div>';
							if (m.isRead == 0) {
								$mHtml += '<i class="icon_mail_alt red"></i>';
							} else {
								$mHtml += '<i class="icon_mail_alt gray"></i>';
							}
							$mHtml += '<label>' + m.createDate + '</label>';
							$mHtml += '</div>';
							$mHtml += '<div>';
							$mHtml += '<p>' + m.content + '</p>';
							$mHtml += '</div>';
							$mHtml += '<div class="font_small">';
							$mHtml += '<label style="float: right;"><a>回复</a>&nbsp;<a>删除</a></label>';
							$mHtml += '</div>';
							$mHtml += '</div>';
							$html += $mHtml;
						});
		break;
	}

	$(".msg_list").html($html);

}
$(document).ready(function() {
	$msg.promise().done(function() {
		var isRead = ($(".msg_list").find(".red").length > 0);
		if (isRead)
			$(".li_active").find(".mark").css("visibility", "visible");
	});
	$note.promise().done(function() {

	});
	$notice.promise().done(function() {

	});
	// tab page
	$(".tab_ul").on('click', 'li', function() {
		var $selected = $(this);
		var unReadMsgs = $(".msg_list").find(".red");
		if (unReadMsgs != null && unReadMsgs.length > 0) {
			var ids = "";
			for (var i = 0; i < unReadMsgs.length; i++) {
				if (i == unReadMsgs.length - 1) {
					var d = unReadMsgs[i];
					ids += $(d).parent().parent().attr("id");
				} else
					ids += unReadMsgs[i] + ";";
			}
			$.ajax({
				type : "post",
				async : true,
				url : '/api/v1/message/read',
				data : {
					msgIds : ids
				},
				beforeSend : function(xhr) {
					var $u = jQuery.parseJSON($.session.get("user"));
					if (!checkNull(userInfo)) {
						xhr.setRequestHeader('token', $u.token);
						xhr.setRequestHeader('userId', $u.userId);
					}
				}
			});
		}
		var $m = $selected.find(".mark");
		if ($m.css("visibility") == 'visible') {
			$m.css("visibility", 'hidden');
		}
		$.each($selected.siblings(), function(i, v) {
			$(v).removeClass('li_active');
		});
		$selected.addClass('li_active');
		var $ds = $selected.attr("data-select");
		if ($ds == "0") {
			$.ajax({
				type : "get",
				async : true,
				url : '/api/v1/message/type/0',
				data : {
					userId : userInfo.userId
				},
				beforeSend : function(xhr) {
					var $u = jQuery.parseJSON($.session.get("user"));
					xhr.setRequestHeader('pageNum', 1);
					if (!checkNull(userInfo)) {
						xhr.setRequestHeader('token', $u.token);
						xhr.setRequestHeader('userId', $u.userId);
					}
				},
				success : function(data) {
					var obj = jQuery.parseJSON(data);
					if (obj.code == '0' && !checkNull(obj.data)) {
						return handleMsg(0, obj.data)
					} else {
						$(".msg_list").html(blankPage);
					}
				}
			});
		} else if ($ds == "1") {
			$.ajax({
				type : "get",
				async : true,
				url : '/api/v1/message/type/1',
				data : {
					userId : userInfo.userId
				},
				beforeSend : function(xhr) {
					var $u = jQuery.parseJSON($.session.get("user"));
					xhr.setRequestHeader('pageNum', 1);
					if (!checkNull(userInfo)) {
						xhr.setRequestHeader('token', $u.token);
						xhr.setRequestHeader('userId', $u.userId);
					}
				},
				success : function(data) {
					var obj = jQuery.parseJSON(data);
					if (obj.code == '0' && !checkNull(obj.data)) {
						return handleMsg(1, obj.data)
					} else {
						$(".msg_list").html(blankPage);
					}
				}
			});
		} else if ($ds == "2") {
			$.ajax({
				type : "get",
				async : true,
				url : '/api/v1/message/type/2',
				data : {
					userId : userInfo.userId
				},
				beforeSend : function(xhr) {
					var $u = jQuery.parseJSON($.session.get("user"));
					xhr.setRequestHeader('pageNum', 1);
					if (!checkNull(userInfo)) {
						xhr.setRequestHeader('token', $u.token);
						xhr.setRequestHeader('userId', $u.userId);
					}
				},
				success : function(data) {
					var obj = jQuery.parseJSON(data);
					if (obj.code == '0' && !checkNull(obj.data)) {
						return handleMsg(2, obj.data)
					} else {
						$(".msg_list").html(blankPage);
					}
				}
			});
		}
	});

});
