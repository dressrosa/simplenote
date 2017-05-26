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
				$(".img_shadow").find("img").attr('src', ar.user.avatar);
				$(".quote").find("span").html(ar.user.description);

				var $mainRight = $(".main_right");
				$mainRight.find(".note_date").html(ar.createDate);
				$mainRight.find(".note_time").html(ar.createTime);
				$mainRight.find("#readNum").html(ar.readNum);
				$mainRight.find(".note_content").attr("id", ar.articleId);
				$mainRight.find(".note_content").html(ar.content);
				$mainRight.find(".note_username").find(".nickname").html(
						ar.user.nickname);
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
	var userInfo = jQuery.parseJSON($.session.get("user"));
	if (checkNull(userInfo)) {
		new jBox('Notice', {
			color : 'red',
			animation : 'tada',
			autoClose : 800,
			content : '请先登录!'
		});
		return false;
	}
	var content = $("#articleContent");
	if (checkNull(content.val())) {
		new jBox('Notice', {
			color : 'red',
			animation : 'tada',
			autoClose : 800,
			content : '请君留字!'
		});
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
			new jBox('Notice', {
				color : 'red',
				animation : 'tada',
				content : '发表失败!'
			});
			return false;
		},
		success : function(data) {
			var jsonObj = jQuery.parseJSON(data);
			if (jsonObj.code == '0') {
				window.location.href = "/article/" + jsonObj.data;
			}
			if (jsonObj.code = '20001') {
				$.session.remove('user');
				new jBox('Notice', {
					color : 'red',
					animation : 'tada',
					content : jsonObj.message
				});
			} else {
				new jBox('Notice', {
					color : 'red',
					animation : 'tada',
					content : jsonObj.message
				});
			}
			return true;
		}
	});
}
$(document).ready(function() {
	$("#login").bind("click", function() {
		gotoLogin('/article/' + articleId);
	});
	$ajaxPromise.promise().done(function() {
		var item = $(".note_content").attr("id");
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