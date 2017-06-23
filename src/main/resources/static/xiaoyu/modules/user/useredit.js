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
				setTitle($user.nickname + '-编辑资料');
				var $userPanel = $(".panel-default");
				if (checkNull($user.avatar)) {
					$user.avatar = 'common/avatar.png';
				}
				$userPanel.find("img").attr("src", $user.avatar);
				$userPanel.find("img").attr("id", $user.userId);
				$userPanel.find(".nickname_panel").html($user.nickname);
				$userPanel.find(".des_panel").html($user.description);

			}
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
	}

	addHeadForImg();
	return true;

};

var handleCollected = function(data) {

	var obj = jQuery.parseJSON(data);
	if (obj.code == '0') {
	}

	addHeadForImg();
	return true;

};

$(document).ready(function() {
	$ajaxPromise1.promise().done(function() {
	});

	$("#login").bind("click", function() {
		gotoLogin('/user/' + userId);
	});
	$("#item_name").find(".info_input").focus(function() {
		$("#item_name").find(".sub_btn").css("display", "initial");
	});
	$("#item_name").find(".info_input").blur(function() {
		$("#item_name").find(".sub_btn").css("display", "none");
	});
	$("#item_sign").find(".info_input").focus(function() {
		$("#item_sign").find(".sub_btn").css("display", "initial");
	});
	$("#item_sign").find(".info_input").blur(function() {
		$("#item_sign").find(".sub_btn").css("display", "none");
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