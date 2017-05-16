var confirmjBox;
$(document).ready(function() {
	confirmjBox = new jBox('Confirm', {
		confirmButton : '确认',
		cancelButton : '取消'
	});
});
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
			url : "/html/back/uploadFile.html",
			reload : true
		},
		onCloseComplete : function(e) {
			myModal.destroy();
		},
	});
	myModal.open();
}
/**
 * login
 */
var num = 0;
function login(item) {
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
		//cache : false,
		type : "POST",
		url : item,
		data : $('#xyForm').serialize(),
		async : true,
		error : function(data) {
			new jBox('Notice', {
				color : 'red',
				animation : 'tada',
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
					url : '/app/user/loginRecord',
					data : {
						userId : jsonObj.data.id
					}
				});
				//console.log( JSON.stringify(jsonObj.data));
				$.session.set('user',JSON.stringify(jsonObj.data), false);
				window.location.href = "/modules/back/user/userDetail.html";
				return true;
			} else {
				new jBox('Notice', {
					color : 'red',
					animation : 'tada',
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
		url : "/app/user/logout",
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