var num = 0;
var login = function() {
	var tip = '姓名和密码都不能为空（*＾-＾*）';
	if ($("#password").val() == '' || $("#loginName").val() == '') {
		if (num > 3 && num < 6) {
			tip = '能不能认真点输,老是不对←_←';
		} else if (num >= 6 && num < 10) {
			tip = '我严重怀疑你到底有没有注册';
		} else if (num >= 10) {
			tip = '我认为你在玩我,我要保留对你的feel'
			if (num == 14)
				num = 0;
		}
		$('.tooltip').jBox('Tooltip', {
			content : tip,
			pointer : false,
			animation : 'zoomIn',
			attch : $("#xyForm"),
			closeOnClick : 'body',
			target : $("#xyForm")
		}).open();

		num++;
		return;
	}
	$.ajax({
		type : "post",
		url : '/api/v1/user/login',
		data : $('#xyForm').serialize(),
		async : true,
		error : function(data) {
			tip = "服务器错误"
			$('.tooltip').jBox('Tooltip', {
				content : tip,
				pointer : false,
				animation : 'zoomIn',
				attch : $("#xyForm"),
				closeOnClick : 'body',
				target : $("#xyForm")
			}).open();
			return false;
		},
		success : function(data) {
			var jsonObj = jQuery.parseJSON(data);
			console.log(jsonObj);

			if (jsonObj.code == '0') {
				// record user ip
				$.ajax({
					type : 'post',
					async : true,
					url : '/api/v1/user/loginRecord',
					data : {
						userId : jsonObj.data.userId,
						device : getDevice()
					},
					beforeSend : function(xhr) {
						var userInfo = jQuery.parseJSON($.session.get("user"));
						if (!checkNull(userInfo)) {
							xhr.setRequestHeader('token', userInfo.token);
						}

					}// 这里设置header
				});
				// console.log( JSON.stringify(jsonObj.data));
				// save the login info
				$.session.set('user', JSON.stringify(jsonObj.data), false);
				// go to the previous page,or go to the home
				var nowUrl = $.session.get("nowUrl");
				console.log("跳转地址:" + nowUrl);
				if (checkNull(nowUrl)) {
					window.location.href = "/xiaoyu.me.html";
				} else {
					$.session.remove("nowUrl");
					window.location.href = nowUrl;
				}

				return true;
			} else {
				$('.tooltip').jBox('Tooltip', {
					content : jsonObj.message,
					pointer : false,
					animation : 'zoomIn',
					attch : $("#xyForm"),
					closeOnClick : 'body',
					target : $("#xyForm")
				}).open();

				return false;
			}
		}
	});
};
var register = function() {
	var tip = '姓名和密码都不能为空（*＾-＾*）';
	var $pwd1 = $("#password1");
	var $name1 = $("#loginName1");
	var $repwd1 = $("#repassword");
	if ($pwd1.val() == '' || $name1.val() == '' || $repwd1.val() == '') {
		tip = "不能不填哦"
		$('.tooltip').jBox('Tooltip', {
			content : tip,
			pointer : false,
			animation : 'zoomIn',
			attch : $("#xyForm1"),
			closeOnClick : 'body',
			target : $("#xyForm1")
		}).open();
		return;
	}
	if (!isEmail($name1.val()) && !isMobile($name1.val())) {
		tip = "请填写正确的邮箱或手机号哦"
		$('.tooltip').jBox('Tooltip', {
			content : tip,
			pointer : false,
			animation : 'zoomIn',
			attch : $("#xyForm1"),
			closeOnClick : 'body',
			target : $("#xyForm1")
		}).open();
		return;
	}
	if (!checkPwd($pwd1.val())) {
		tip = "密码长度至少6位哦"
		$('.tooltip').jBox('Tooltip', {
			content : tip,
			pointer : false,
			animation : 'zoomIn',
			attch : $("#xyForm1"),
			closeOnClick : 'body',
			target : $("#xyForm1")
		}).open();
		return;
	}
	if ($pwd1.val() != $repwd1.val()) {
		tip = "密码填写不一致哦"
		$('.tooltip').jBox('Tooltip', {
			content : tip,
			pointer : false,
			animation : 'zoomIn',
			attch : $("#xyForm1"),
			closeOnClick : 'body',
			target : $("#xyForm1")
		}).open();
		return;
	}

	$.ajax({
		type : "post",
		url : '/api/v1/user/register',
		data : $('#xyForm1').serialize(),
		async : true,
		error : function(data) {
			tip = "服务器错误";
			$('.tooltip').jBox('Tooltip', {
				content : tip,
				pointer : false,
				animation : 'zoomIn',
				attch : $("#xyForm1"),
				closeOnClick : 'body',
				target : $("#xyForm1")
			}).open();
			$(".registering").removeAttr("disabled");
			return false;
		},
		success : function(data) {
			var jsonObj = jQuery.parseJSON(data);
			if (jsonObj.code == '0') {
				$(".registerform").css("display", "none");
				$(".loginform").css("display", "block");
				$('.tooltip').jBox('Tooltip', {
					content : "注册成功,不如登录看看吧",
					pointer : false,
					animation : 'zoomIn',
					attch : $("#xyForm"),
					closeOnClick : 'body',
					target : $("#xyForm")
				}).open();
				$(".registering").removeAttr("disabled");
				return true;
			} else {
				$('.tooltip').jBox('Tooltip', {
					content : jsonObj.message,
					pointer : false,
					animation : 'zoomIn',
					attch : $("#xyForm1"),
					closeOnClick : 'body',
					target : $("#xyForm1")
				}).open();
				$(".registering").removeAttr("disabled");
				return false;
			}

		}
	});
	$(".registering").attr("disabled", "disabled");
}

var showPwd = function(item) {
	var pwd = $(".pwd");
	if (pwd.attr("type") == "password") {
		pwd.attr("type", "text");
		$(item).css("color", "#de5252b5");
	} else {
		pwd.attr("type", "password");
		$(item).css("color", "#c1b6b6");
	}
};
$(document).ready(function() {
	if (!isPC()) {

	}

	$(".logining").bind("click", login);
	$(".registering").bind("click", register);
	$(".goregister").bind("click", function() {
		$(".loginform").css("display", "none");
		$(".registerform").css("display", "block");
	});
	$(".gologin").bind("click", function() {
		$(".registerform").css("display", "none");
		$(".loginform").css("display", "block");
	});
});
