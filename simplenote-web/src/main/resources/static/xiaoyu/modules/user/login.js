var num = 0;
var login = function() {
    var tip = '信息不能为空';
    if ($("#password").val() == '' || $("#loginName").val() == '') {
        if (num > 3 && num < 6) {
            tip = '能不能认真点,老是不对';
        } else if (num >= 6 && num < 10) {
            tip = '我严重怀疑你到底有没有注册';
        } else if (num >= 10) {
            tip = '我感觉你在玩我啊'
            if (num == 14)
                num = 0;
        }
        loginTip(tip, "xyForm");
        num++;
        return;
    }
    $.ajax({
        type : "post",
        url : '/api/v1/user/login',
        data : $('#xyForm').serialize(),
        cache : false,
        async : true,
        error : function(data) {
            tip = "服务器错误"
            loginTip(tip, "xyForm");
            return false;
        },
        success : function(data) {
            var jsonObj = jQuery.parseJSON(data);
            if (jsonObj.code != '0') {
                loginTip(jsonObj.message, "xyForm");
                return false;
            }
            // record user ip
            $.ajax({
                type : 'post',
                async : true,
                url : '/api/v1/user/login/record',
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
            // save the login info
            $.session.set('user', JSON.stringify(jsonObj.data), true);
            // go to the previous page,or go to the home
            var nowUrl = $.session.get("nowUrl");
            if (checkNull(nowUrl) || nowUrl == 'null') {
                window.location.href = "/xiaoyu.me.html";
            } else {
                $.session.remove("nowUrl");
                window.location.href = nowUrl;
            }
            return true;
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
        loginTip(tip, "xyForm1");
        return;
    }
    if (!isEmail($name1.val()) && !isMobile($name1.val())) {
        tip = "请填写正确的邮箱或手机号哦"
        loginTip(tip, "xyForm1");
        return;
    }
    if (!checkPwd($pwd1.val())) {
        tip = "密码长度至少6位哦"
        loginTip(tip, "xyForm1");
        return;
    }
    if ($pwd1.val() != $repwd1.val()) {
        tip = "密码填写不一致哦"
        loginTip(tip, "xyForm1");
        return;
    }

    $.ajax({
        type : "post",
        url : '/api/v1/user/register',
        data : $('#xyForm1').serialize(),
        async : true,
        error : function(data) {
            tip = "服务器错误";
            loginTip(tip, "xyForm1");
            $(".registering").removeAttr("disabled");
            return false;
        },
        success : function(data) {
            var jsonObj = jQuery.parseJSON(data);
            if (jsonObj.code == '0') {
                $(".registerform").css("display", "none");
                $(".loginform").css("display", "block");
                loginTip("注册成功,不如登录看看吧", "xyForm");
                $(".registering").removeAttr("disabled");
                return true;
            } else {
                loginTip(jsonObj.message, "xyForm1");
                $(".registering").removeAttr("disabled");
                return false;
            }

        }
    });
    $(".registering").attr("disabled", "disabled");
}
var $tipBox = null;
var loginTip = function(msg, location) {
    if ($tipBox != null) {
        // 判断是否前一次的没有清除
        $tipBox.destroy();
    }
    if (isPC()) {
        $('.tooltip').jBox('Tooltip', {
            content : msg,
            pointer : false,
            animation : 'zoomIn',
            attch : $("#" + location),
            closeOnClick : 'body',
            target : $("#" + location)
        }).open();
    } else {
        $tipBox = new jBox('Notice', {
            content : msg,
            animation : 'pulse',
            position : {
                x : 'center',
                y : 'center'
            },
            autoClose : 1000,
            closeOnClick : 'body'
        });
        $tipBox.open();
    }

};
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
