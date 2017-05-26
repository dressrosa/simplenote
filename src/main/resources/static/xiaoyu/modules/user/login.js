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
			attach : $("#xyForm"),
			closeOnClick : 'body',
			color : 'red',
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
			new jBox('Notice', {
				color : 'red',
				animation : 'tada',
				autoClose : 1000,
				content : '服务器错误'
			});
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
				new jBox('Notice', {
					color : 'red',
					animation : 'tada',
					autoClose : 1000,
					content : jsonObj.message
				});
				return false;
			}
		}
	});
};


var showPwd = function(item) {
	var pwd = $("#password");
	if (pwd.attr("type") == "password") {
		pwd.attr("type", "text");
		item.innerHTML = '隐藏';
	} else {
		pwd.attr("type", "password");
		item.innerHTML = '可见';
	}
};
$(document).ready(function() {
	if (isPC()) {
		$("#xyForm").css("width", "20%");
	} else {
		$("#xyForm").css("width", "100%");
	}
	$(".logining").bind("click",login);
});
