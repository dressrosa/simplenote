var confirmjBox;
$(document).ready(
		function() {
			getDevice();
			var userInfo = jQuery.parseJSON($.session.get('user'));
			if (checkNull(userInfo)) {
				$("#loginSpan").css("display", "block");
			} else {
				$("#userSpan").css("display", "block");
				$("#userSpan").on("mouseover", function() {
					$(this).find("ul").css("display", "block");
				});
				$("#userSpan").on("mouseout", function() {
					$(this).find("ul").css("display", "none");
				});

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
	window.location.href = "/login";
}
/* logout */
var logout = function() {
	$.session.remove('user');
	$.ajax({
		type : "post",
		url : "/api/v1/user/logout",
		success : function() {
			window.location.href = window.location.href.replace(/#/g, '');
		}
	});
}
function isEmail(str) {
	var re = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/
	if (re.test(str))
		return true;
	return false;

}
function checkPwd(str) {
	if (str.length >= 6)
		return true;
	return false;
}
function isMobile(str) {
	var re = /^1\d{10}$/
	if (re.test(str))
		return true;
	return false;
}
function getAgent() {
	var agent = navigator.userAgent.toLowerCase();
	return agent;
}
function getDevice() {
	var agent = navigator.userAgent.toLowerCase();
	var osName = function() {
		if (/windows/.test(agent)) {
			return 'windows';
		} else if (/iphone|ipod|ipad|ios/.test(agent)) {
			return 'ios';
		} else if (/android/.test(agent)) {
			return 'android';
		} else if (/linux/.test(agent)) {
			return 'linux';
		}
	};
	return osName();

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
	if (item == null || item == u ndefined || item.trim == '')
		return true;
	return false;
}
