var confirmjBox;
$(document).ready(
		function() {
			confirmjBox = new jBox('Confirm', {
				confirmButton : '确认',
				cancelButton : '取消'
			});

			var userInfo = jQuery.parseJSON($.session.get('user'));
			if (checkNull(userInfo)) {
				$("#loginSpan").css("display", "block");
			} else {
				$("#userSpan").css("display", "block");
				$("#userSpan").find("#nickname").attr("href",
						"/user/" + userInfo.userId);
				$("#userSpan").find("#nickname").text(userInfo.nickname);
			}
		});


function setTitle(item) {
	document.title = item;
}
/**
 * 更新信息
 * 
 * @param item
 */
function update(item) {// 传入action
	$.ajax({
		cache : true,
		type : "POST",
		url : item,
		data : $('#xyForm').serialize(),
		async : false,
		error : function(data) {
			new jBox('Notice', {
				color : 'red',
				animation : 'tada',
				content : '服务器错误!'
			});
			return false;
		},
		success : function(data) {
			if (data == 'success') {
				new jBox('Notice', {
					color : 'red',
					animation : 'tada',
					content : '更新成功!'
				});
				confirmjBox.close();
			} else {
				new jBox('Notice', {
					color : 'red',
					animation : 'tada',
					content : '更新失败!'
				});
			}
			return true;
		}
	});
};
/**
 * 提交表单
 * 
 * @param item
 */
function postForm(item) {
	$.ajax({
		cache : true,
		type : "POST",
		url : item,
		data : $('#xyForm').serialize(),
		async : false,
		error : function(data) {
			new jBox('Notice', {
				color : 'red',
				animation : 'tada',
				content : '服务器错误!'
			});
			return false;
		},
		success : function(data) {
			if (data == 'success') {
				new jBox('Notice', {
					color : 'red',
					animation : 'tada',
					content : '添加成功!'
				});
			} else {
				new jBox('Notice', {
					color : 'red',
					animation : 'tada',
					content : '添加失败!'
				});
			}
			return true;
		}
	});
}

/**
 * 根据id查看详情
 * 
 * @param item
 */
function getDetail(action, item) {
	new jBox('Modal', {
		width : 700,
		height : 600,
		title : "详细信息",
		closeButton : 'title',
		closeOnClick : false,
		draggable : "title",
		ajax : {
			url : action,
			data : 'id=' + item,
			reload : true
		}
	}).open();
}
var myModal;// 定义全局变量,用作弹出窗口上传后,关闭弹出框
function uploadFile() {
	if (myModal != null) {// 判断是否前一次的没有清除
		myModal.destroy();
	}
	myModal = new jBox('Modal', {
		height : 350,
		width : 350,
		animation : 'flip',
		closeButton : 'title',
		closeOnClick : false,
		draggable : "title",
		title : "上传图片",
		ajax : {
			url : "/back/uploadFile.html",
			reload : true
		},
		onCloseComplete : function(e) {
			myModal.destroy();
		},
	});
	myModal.open();
}
/*
 * 转向登陆页面,并记录当前页面的地址
 */
function gotoLogin(nowUrl) {
	$.session.set('nowUrl', nowUrl, true);
	window.location.href = "/common/login.html";
}
/**
 * login
 */
var num = 0;
function login() {
	var tip = '姓名和密码都不能为空（*＾-＾*）';
	if ($("#password").val() == '' || $("#loginName").val() == '') {
		if (num > 3 && num < 6) {
			tip = '能不能认真点输,老是不对←_←';
		} else if (num >= 6 && num < 10) {
			tip = '我严重怀疑你到底有没有注册';
		} else if (num >= 10) {
			tip = '我认为你在玩我,我要保留对你的feel'
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
						userId : jsonObj.data.id
					},
					beforeSend : function(xhr) {
						var userInfo = $.session.get("user");
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
/* logout */
function logout() {
	$.session.remove('user');
	// window.location.href = window.location.href.replace(/#/g,'');
	$.ajax({
		type : "POST",
		url : "/private/user/logout",
		success : function() {
			window.location.href = window.location.href.replace(/#/g, '');
		}
	});
}

// tool function

/**
 * 时间转化
 * 
 * @param time
 * @returns {String}
 */
function D2Str(time) {
	var datetime = new Date();
	datetime.setTime(time);
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1)
			: datetime.getMonth() + 1;
	var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime
			.getDate();
	var hour = datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime
			.getHours();
	var minute = datetime.getMinutes() < 10 ? "0" + datetime.getMinutes()
			: datetime.getMinutes();
	var second = datetime.getSeconds() < 10 ? "0" + datetime.getSeconds()
			: datetime.getSeconds();
	return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":"
			+ second;
}

function isPC() {
	var userAgentInfo = navigator.userAgent;
	var Agents = [ "Android", "iPhone", "SymbianOS", "Windows Phone", "iPad",
			"iPod" ];
	var flag = true;
	for (var v = 0; v < Agents.length; v++) {
		if (userAgentInfo.indexOf(Agents[v]) > 0) {
			flag = false;
			break;
		}
	}
	return flag;
}
function checkNull(item) {
	if (item == null || item == 'null' || item == undefined || item == '')
		return true;
	return false;
}
